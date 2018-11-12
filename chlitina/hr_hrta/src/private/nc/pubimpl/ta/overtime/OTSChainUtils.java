package nc.pubimpl.ta.overtime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.impl.ta.overtime.SegdetailMaintainImpl;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.AggSegDetailVO;
import nc.vo.ta.overtime.SegDetailConsumeVO;
import nc.vo.ta.overtime.SegDetailVO;
import nc.vo.ta.overtime.SegRuleTermVO;

import org.apache.commons.lang.StringUtils;

/**
 * 加班分段明細雙向鏈表工具類
 * 
 * @author ssx
 * 
 */
public class OTSChainUtils {
    private static BaseDAO baseDAO = null;
    protected static String SPLT = "::";

    /**
     * 根據人員及加班類別創建現有記錄創建鏈表
     * 
     * @param pk_psndoc
     *            人員PK
     * @param otDate
     *            加班日期
     * @param pk_overtimereg
     *            加班登記PK
     * @param isForceComp
     *            是否只建立轉調休記錄，True時只建立轉調休的節點
     * @param isNoComp
     *            是否只建立非轉調休記錄，True時建立除轉調休以外其他節點
     * @param isForceNotCancel
     *            是否只建立未作廢記錄，True時只建立未作廢的節點
     * @param isForceNotConsumeFinished
     *            是否只建立未核銷完畢的記錄，True時只建立未核銷完畢的節點
     * @param isForceSettled
     *            是否只建立未結算的記錄，True時只建立未結算的節點
     * @return 鏈表頭節點
     * 
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    public static OTSChainNode buildChainNodes(String pk_psndoc, UFLiteralDate otDate, String pk_overtimereg,
	    boolean isForceComp, boolean isNoComp, boolean isForceNotCancel, boolean isForceNotConsumeFinished,
	    boolean isForceSettled) throws BusinessException {
	// 按人員取全節點
	// Collection<SegDetailVO> sdList = getBaseDAO().retrieveByClause(
	// SegDetailVO.class,
	// "dr=0 and pk_psndoc='" + pk_psndoc + "' "
	// + (otDate == null ? "" : " and regdate='" + otDate.toString() + "'")
	// + (StringUtils.isEmpty(pk_overtimereg) ? "" : " and pk_overtimereg='"
	// + pk_overtimereg + "'"),
	// "nodeno");
	List<Map<String, Object>> vodataList = (List<Map<String, Object>>) getBaseDAO()
		.executeQuery(
			"select pk_segdetail,pk_group,pk_org,pk_org_v,creator,creationtime,modifier,modifiedtime,maketime,"
				+ "pk_parentsegdetail,nodeno,code,name,regdate,pk_segrule,pk_segruleterm,pk_psndoc,pk_overtimereg,"
				+ "rulehours,hours,hourstaxfree,hourstaxable,hourlypay,taxfreerate,taxablerate,consumedhours,"
				+ "consumedhourstaxfree,consumedhourstaxable,remaininghours,remainhourstaxfree,"
				+ "remainhourstaxable,remainingamount,remainamounttaxfree,remainamounttaxable,iscanceled,"
				+ "iscompensation,hourstorest,isconsumed,issettled,frozenhours,frozenhourstaxfree,frozenhourstaxable,settledate,expirydate,approveddate,ts,dr from "
				+ SegDetailVO.getDefaultTableName()
				+ " where dr=0 and pk_psndoc='"
				+ pk_psndoc
				+ "' "
				+ (otDate == null ? "" : " and regdate='" + otDate.toString() + "'")
				+ (StringUtils.isEmpty(pk_overtimereg) ? "" : " and pk_overtimereg='" + pk_overtimereg
					+ "'"), new MapListProcessor());

	OTSChainNode firstNode = null;
	List<String> pkList = new ArrayList<String>();
	if (vodataList != null && vodataList.size() > 0) {
	    SegDetailVO[] sdList = getSegDetailVOsFromVOData(vodataList);
	    for (SegDetailVO vo : sdList) {
		vo.setPk_segdetailconsume(getConsumeVO(vo.getPk_segdetail()));
		// 查找第一個節點
		if (vo.getPk_parentsegdetail() == null || !existsParent(vo, sdList)) {
		    firstNode = new OTSChainNode();
		    firstNode.setNodeData(vo);
		    firstNode.setNextNode(null);
		    firstNode.setPriorNode(null);
		    pkList.add(vo.getPk_segdetail());
		}
	    }

	    if (firstNode != null) {
		OTSChainNode curNode = firstNode;
		// 構建鏈表
		for (SegDetailVO vo : sdList) {
		    if (!pkList.contains(vo.getPk_segdetail())) {
			SegDetailVO childVO = getChildVO(sdList, curNode.getNodeData().getPk_segdetail());
			if (childVO != null) {
			    OTSChainNode newNode = new OTSChainNode();
			    newNode.setNodeData(childVO);
			    appendNode(curNode, newNode);
			    curNode = newNode;
			} else {
			    break; // 一旦找不到子節點，認為鏈表已建立完畢
			}

			pkList.add(vo.getPk_segdetail());
		    }
		}
	    }
	}

	// 如果有過濾條件
	if (isForceComp || isForceNotCancel || isForceNotConsumeFinished || isForceSettled) {
	    firstNode = filterNodes(isForceComp, isNoComp, isForceNotCancel, isForceNotConsumeFinished, isForceSettled,
		    firstNode);
	}

	return firstNode;
    }

    private static SegDetailVO[] getSegDetailVOsFromVOData(List<Map<String, Object>> vodataList) {
	List<SegDetailVO> ret = new ArrayList<SegDetailVO>();
	for (Map<String, Object> vodata : vodataList) {
	    SegDetailVO vo = getVOByVOData(vodata);
	    ret.add(vo);
	}
	return ret.toArray(new SegDetailVO[0]);
    }

    private static SegDetailVO getVOByVOData(Map<String, Object> vodata) {
	SegDetailVO vo = new SegDetailVO();
	vo.setPk_segdetail((String) vodata.get("pk_segdetail"));
	vo.setPk_group((String) vodata.get("pk_group"));
	vo.setPk_org((String) vodata.get("pk_org"));
	vo.setPk_org_v((String) vodata.get("pk_org_v"));
	vo.setCreator((String) vodata.get("creator"));
	vo.setCreationtime(getUFDateTime(vodata.get("creationtime")));
	vo.setModifier((String) vodata.get("modifier"));
	vo.setModifiedtime(getUFDateTime(vodata.get("modifiedtime")));
	vo.setMaketime(vodata.get("maketime") == null ? null : new UFDate((String) vodata.get("maketime")));
	vo.setPk_parentsegdetail((String) vodata.get("pk_parentsegdetail"));
	vo.setNodeno((Integer) vodata.get("nodeno"));
	vo.setNodecode((String) vodata.get("code"));
	vo.setNodename((String) vodata.get("name"));
	vo.setRegdate(vodata.get("regdate") == null ? null : new UFLiteralDate((String) vodata.get("regdate")));
	vo.setPk_segrule((String) vodata.get("pk_segrule"));
	vo.setPk_segruleterm((String) vodata.get("pk_segruleterm"));
	vo.setPk_psndoc((String) vodata.get("pk_psndoc"));
	vo.setPk_overtimereg((String) vodata.get("pk_overtimereg"));
	vo.setRulehours(getUFDoubleValue(vodata.get("rulehours")));
	vo.setHours(getUFDoubleValue(vodata.get("hours")));
	vo.setHourstaxfree(getUFDoubleValue(vodata.get("hourstaxfree")));
	vo.setHourstaxable(getUFDoubleValue(vodata.get("hourstaxable")));
	vo.setHourlypay(getUFDoubleValue(vodata.get("hourlypay")));
	vo.setTaxfreerate(getUFDoubleValue(vodata.get("taxfreerate")));
	vo.setTaxablerate(getUFDoubleValue(vodata.get("taxablerate")));
	vo.setConsumedhours(getUFDoubleValue(vodata.get("consumedhours")));
	vo.setConsumedhourstaxfree(getUFDoubleValue(vodata.get("consumedhourstaxfree")));
	vo.setConsumedhourstaxable(getUFDoubleValue(vodata.get("consumedhourstaxable")));
	vo.setRemainhours(getUFDoubleValue(vodata.get("remaininghours")));
	vo.setRemainhourstaxfree(getUFDoubleValue(vodata.get("remainhourstaxfree")));
	vo.setRemainhourstaxable(getUFDoubleValue(vodata.get("remainhourstaxable")));
	vo.setRemainamount(getUFDoubleValue(vodata.get("remainingamount")));
	vo.setRemainamounttaxfree(getUFDoubleValue(vodata.get("remainamounttaxfree")));
	vo.setRemainamounttaxable(getUFDoubleValue(vodata.get("remainamounttaxable")));
	vo.setFrozenhours(getUFDoubleValue(vodata.get("frozenhours")));
	vo.setFrozenhourstaxfree(getUFDoubleValue(vodata.get("frozenhourstaxfree")));
	vo.setFrozenhourstaxable(getUFDoubleValue(vodata.get("frozenhourstaxable")));
	vo.setIscanceled(new UFBoolean("Y".equals((String) vodata.get("iscanceled"))));
	vo.setIscompensation(new UFBoolean("Y".equals((String) vodata.get("iscompensation"))));
	vo.setHourstorest(getUFDoubleValue(vodata.get("hourstorest")));
	vo.setIsconsumed(new UFBoolean("Y".equals((String) vodata.get("isconsumed"))));
	vo.setIssettled(new UFBoolean("Y".equals((String) vodata.get("issettled"))));
	vo.setSettledate(getUFLiteralDate(vodata.get("settledate")));
	vo.setApproveddate(getUFLiteralDate(vodata.get("approveddate")));
	vo.setExpirydate(getUFLiteralDate(vodata.get("expirydate")));
	vo.setTs(getUFDateTime(vodata.get("ts")));
	vo.setDr(vodata.get("dr") == null ? null : (Integer) vodata.get("dr"));
	return vo;
    }

    private static UFLiteralDate getUFLiteralDate(Object value) {
	if (value == null) {
	    return null;
	} else {
	    return new UFLiteralDate((String) value);
	}
    }

    private static UFDateTime getUFDateTime(Object value) {
	if (value == null) {
	    return null;
	} else {
	    return new UFDateTime((String) value);
	}
    }

    private static UFDouble getUFDoubleValue(Object value) {
	if (value == null) {
	    return UFDouble.ZERO_DBL;
	} else {
	    if (value instanceof BigDecimal) {
		return new UFDouble((BigDecimal) value);
	    } else if (value instanceof Integer) {
		return new UFDouble((Integer) value);
	    } else {
		return new UFDouble(String.valueOf(value));
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private static SegDetailConsumeVO[] getConsumeVO(String pk_segdetail) throws BusinessException {
	Collection<SegDetailConsumeVO> consumevos = getBaseDAO().retrieveByClause(SegDetailConsumeVO.class,
		"pk_segdetail='" + pk_segdetail + "'");
	return consumevos == null ? null : consumevos.toArray(new SegDetailConsumeVO[0]);
    }

    /**
     * 按參數從物理鏈表中過濾出邏輯鏈表
     * 
     * @param isForceComp
     *            是否只建立轉調休記錄，True時只建立轉調休的節點
     * @param isNoComp
     *            是否只建立非轉調休記錄，True時建立除轉調休外其他所有節點
     * @param isForceNotCancel
     *            是否只建立未作廢記錄，True時只建立未作廢的節點
     * @param isForceNotConsumeFinished
     *            是否只建立未核銷完畢的記錄，True時只建立未核銷完畢的節點
     * @param isForceSettled
     *            是否只建立未結算的記錄，True時只建立未結算的節點
     * @param firstNode
     *            第一個節點
     * @return 第一個節點
     * @throws BusinessException
     */
    public static OTSChainNode filterNodes(boolean isForceComp, boolean isNoComp, boolean isForceNotCancel,
	    boolean isForceNotConsumeFinished, boolean isForceSettled, OTSChainNode firstNode) throws BusinessException {
	if (firstNode != null) {
	    OTSChainNode curNode = firstNode.clone(); // 克隆節點，以免因改動造成其他邏輯鏈表混亂
	    while (curNode.getNextNode() != null) {
		OTSChainNode tmpNextNode = curNode.getNextNode();
		if ((isForceComp && !curNode.getNodeData().getIscompensation().booleanValue() // 刪除非加班轉調休節點
			)
			|| (isForceNotCancel && curNode.getNodeData().getIscanceled().booleanValue() // 刪除已作廢節點
			) || (isForceNotConsumeFinished && curNode.getNodeData().getIsconsumed().booleanValue() // 刪除已核銷完畢節點
			) || (isForceSettled && curNode.getNodeData().getIssettled().booleanValue() // 刪除已結算節點
			) || (isNoComp && curNode.getNodeData().getIscompensation().booleanValue()) // 刪除轉調休節點
		) {
		    removeCurrentNode(curNode, false);
		}

		curNode = tmpNextNode;
	    }

	    firstNode = getFirstNode(curNode);
	}

	return firstNode;
    }

    /**
     * 取子節點
     * 
     * @param sdList
     *            節點列表
     * @param pk_segdetail
     *            當前節點PK
     * @return
     */
    private static SegDetailVO getChildVO(SegDetailVO[] sdList, String pk_segdetail) {
	for (SegDetailVO vo : sdList) {
	    if (pk_segdetail.equals(vo.getPk_parentsegdetail())) {
		return vo;
	    }
	}
	return null;
    }

    /**
     * 給定VO是否不存在上級節點
     * 
     * @param sdList
     *            全節點
     * @param vo
     *            檢查節點
     * @return
     */
    private static boolean existsParent(SegDetailVO vo, SegDetailVO[] sdList) {
	for (SegDetailVO childvo : sdList) {
	    if (vo.getPk_parentsegdetail().equals(childvo.getPk_segdetail())) {
		return true;
	    }
	}
	return false;
    }

    /**
     * 在指定節點後增加節點
     * 
     * @param targetNode
     *            目標節點
     * @param newNode
     *            新增節點
     * @throws BusinessException
     */
    public static OTSChainNode appendNode(OTSChainNode targetNode, OTSChainNode newNode) throws BusinessException {
	if (targetNode != null) {
	    if (targetNode.getNextNode() != null) {
		if (newNode != null) {
		    newNode.setNextNode(targetNode.getNextNode());
		}
		targetNode.getNextNode().setPriorNode(newNode);
	    }
	    targetNode.setNextNode(newNode);
	    if (newNode != null) {
		newNode.setPriorNode(targetNode);
	    }
	    return targetNode;
	} else {
	    return newNode;
	}
    }

    /**
     * 刪除指定節點的下一節點
     * 
     * @param targetNode
     *            目標節點
     * @param removeFromDB
     *            是否從數據庫中刪除
     * @throws BusinessException
     */
    public static void removeNextNode(OTSChainNode targetNode, boolean removeFromDB) throws BusinessException {
	if (targetNode != null) {
	    if (targetNode.getNextNode() != null) {
		removeCurrentNode(targetNode.getNextNode(), removeFromDB);
	    } else {
		throw new BusinessException("節點刪除錯誤：當前節點後已無後繼節點。");
	    }
	} else {
	    throw new BusinessException("節點刪除錯誤：目標節點為空。");
	}
    }

    /**
     * 刪除指定節點的前一節點
     * 
     * @param targetNode
     *            目標節點
     * @param removeFromDB
     *            是否從數據庫中刪除
     * @throws BusinessException
     */
    public static void removePriorNode(OTSChainNode targetNode, boolean removeFromDB) throws BusinessException {
	if (targetNode != null) {
	    if (targetNode.getPriorNode() != null) {
		removeCurrentNode(targetNode.getPriorNode(), removeFromDB);
	    } else {
		throw new BusinessException("節點刪除錯誤：當前節點後已無前續節點。");
	    }
	} else {
	    throw new BusinessException("節點刪除錯誤：目標節點為空。");
	}
    }

    /**
     * 刪除當前節點
     * 
     * @param targetNode
     *            當前要刪除的節點
     * @param removeFromDB
     *            是否從數據庫中刪除
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    public static void removeCurrentNode(OTSChainNode targetNode, boolean removeFromDB) throws BusinessException {
	if (targetNode.getPriorNode() != null) {
	    targetNode.getPriorNode().setNextNode(targetNode.getNextNode());
	}

	if (targetNode.getNextNode() != null) {
	    targetNode.getNextNode().setPriorNode(targetNode.getPriorNode());
	}

	if (removeFromDB) {
	    SegDetailVO vo = targetNode.getNodeData();
	    // 刪除
	    AggSegDetailVO aggvo = new AggSegDetailVO();
	    aggvo.setParent(vo);

	    Collection<SegDetailConsumeVO> lstChildVOs = getBaseDAO().retrieveByClause(SegDetailConsumeVO.class,
		    "pk_segdetail='" + vo.getPk_segdetail() + "'");
	    aggvo.setChildrenVO(lstChildVOs.toArray(new SegDetailConsumeVO[0]));
	    new SegdetailMaintainImpl().delete(new AggSegDetailVO[] { aggvo });
	}
    }

    /**
     * 根據給定節點查找第一個節點
     * 
     * @param node
     *            節點
     * @return 第一個節點
     * @throws BusinessException
     */
    public static OTSChainNode getFirstNode(OTSChainNode node) throws BusinessException {
	if (node != null) {
	    if (node.getPriorNode() == null) {
		return node;
	    } else {
		return getFirstNode(node.getPriorNode());
	    }
	} else {
	    throw new BusinessException("獲取第一個節點錯誤：當前節點不能為空。");
	}
    }

    /**
     * 根據給定的節點查找最後一個節點
     * 
     * @param node
     *            節點
     * @return 最後一個節點
     * @throws BusinessException
     */
    public static OTSChainNode getLastNode(OTSChainNode node) throws BusinessException {
	if (node != null) {
	    if (node.getNextNode() == null) {
		return node;
	    } else {
		return getLastNode(node.getNextNode());
	    }
	} else {
	    throw new BusinessException("獲取最後一個節點錯誤：當前節點不能為空。");
	}
    }

    /**
     * 保存所有節點的加班分段明細 （僅用於對SegDetailVO實體的操作，不處理消耗作業）
     * 
     * @param node
     *            節點
     * @return
     * @throws BusinessException
     */
    public static OTSChainNode saveAll(OTSChainNode node) throws BusinessException {
	if (node != null) {
	    OTSChainNode curNode = getFirstNode(node);
	    do {
		if (curNode.getPriorNode() != null) {
		    curNode.getNodeData().setPk_parentsegdetail(curNode.getPriorNode().getNodeData().getPk_segdetail());
		} else {
		    curNode.getNodeData().setPk_parentsegdetail(null);
		}
		save(curNode);
		curNode = curNode.getNextNode();
	    } while (curNode != null);
	    return node;
	} else {
	    throw new BusinessException("全部保存錯誤：當前節點不能為空。");
	}
    }

    /**
     * 單節點保存
     * 
     * @param node
     *            當前保存的節點
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    public static void save(OTSChainNode node) throws BusinessException {
	SegDetailVO vo = node.getNodeData();
	if (vo.getPk_segdetail() == null) {
	    // 新增
	    AggSegDetailVO aggvo = new AggSegDetailVO();

	    aggvo.setParent(vo);
	    AggSegDetailVO[] ret = new SegdetailMaintainImpl().insert(new AggSegDetailVO[] { aggvo });
	    vo.setPk_segdetail(ret[0].getPrimaryKey());
	} else {
	    // 修改
	    AggSegDetailVO aggvo = new AggSegDetailVO();
	    aggvo.setParent(vo);

	    Collection<SegDetailConsumeVO> lstChildVOs = getBaseDAO().retrieveByClause(SegDetailConsumeVO.class,
		    "pk_segdetail='" + vo.getPk_segdetail() + "'");
	    aggvo.setChildrenVO(lstChildVOs.toArray(new SegDetailConsumeVO[0]));
	    new SegdetailMaintainImpl().update(new AggSegDetailVO[] { aggvo });
	}
    }

    public static BaseDAO getBaseDAO() {
	if (baseDAO == null) {
	    baseDAO = new BaseDAO();
	}

	return baseDAO;
    }

    /**
     * 合併節點
     * 
     * @param originalNode
     *            原始節點
     * @param newNode
     *            新節點
     * @throws BusinessException
     */
    public static OTSChainNode combineNodes(OTSChainNode originalNode, OTSChainNode newNode) throws BusinessException {
	if (newNode != null) {
	    OTSChainNode curNode = null;
	    OTSChainNode parentNode = null;

	    // 按新節點遍歷
	    do {
		if (originalNode == null) {
		    originalNode = newNode;
		    break;
		} else {
		    if (curNode == null) {
			curNode = newNode;
		    } else {
			curNode = curNode.getNextNode();
		    }
		}

		// 查找父節點
		// 基本邏輯：查找NodeCode相同（人員，日期，分段號碼都相同）的節點
		// 或從鏈表末端往前回遡，出現的第一個NodeCode小於當前NodeCode的節點
		parentNode = findParentNode(originalNode, curNode);
		OTSChainNode addedNode = curNode.cloneSingle();
		if (parentNode == null) {
		    // 不存在父節點，即鏈表中所有節點均比當前節點發生的晚，所以當前節點應為首節點
		    addedNode.setNextNode(null);
		    addedNode.setPriorNode(null);
		    originalNode = OTSChainUtils.appendNode(addedNode, originalNode);
		} else {
		    if (parentNode.getNodeData().getNodecode().equals(addedNode.getNodeData().getNodecode())) {
			OTSChainNode nextNewNode = null;
			// 節點編碼相同=請假人員、日期、分段完全一樣
			UFDouble ruleHours = parentNode.getNodeData().getRulehours(); // 分段規則定義的分段時長
			UFDouble parentHours = getParentTotalHoursBySameCode(parentNode); // 已佔用的規則時長
			UFDouble newHours = addedNode.getNodeData().getHours();
			if (parentHours.doubleValue() < ruleHours.doubleValue()) {
			    // 父節點加班時長小於分段時長時=上次請假未請滿該分段，檢查分段纍計時長
			    if (parentHours.add(newHours).doubleValue() <= ruleHours.doubleValue()) {
				// 纍加後仍然小於等於分段時長的，直接增加後續節點
				originalNode = appendNode(parentNode, addedNode);
			    } else {
				// 纍加後超過分段時長的，將增補本段時長的後續節點
				UFDouble appendHours = ruleHours.sub(parentHours); // 未佔用的規則時長
				UFDouble appendHoursTaxfree = UFDouble.ZERO_DBL;
				UFDouble appendHoursTaxable = UFDouble.ZERO_DBL;
				if (appendHours.doubleValue() >= addedNode.getNodeData().getHourstaxfree()
					.doubleValue()) {
				    appendHoursTaxfree = addedNode.getNodeData().getHourstaxfree();
				    if (appendHours.sub(appendHoursTaxfree).doubleValue() >= addedNode.getNodeData()
					    .getHourstaxable().doubleValue()) {
					appendHoursTaxable = addedNode.getNodeData().getHourstaxable();
				    } else {
					appendHoursTaxable = appendHours.sub(appendHoursTaxfree);
				    }
				} else {
				    appendHoursTaxfree = appendHours;
				    appendHoursTaxable = UFDouble.ZERO_DBL;
				}

				UFDouble nextHours = addedNode.getNodeData().getHours().sub(appendHours);
				UFDouble nextHoursTaxfree = addedNode.getNodeData().getHourstaxfree()
					.sub(appendHoursTaxfree);
				UFDouble nextHoursTaxable = addedNode.getNodeData().getHourstaxable()
					.sub(appendHoursTaxable);

				addedNode.getNodeData().setHours(appendHours);
				addedNode.getNodeData().setHourstaxfree(appendHoursTaxfree);
				addedNode.getNodeData().setHourstaxable(appendHoursTaxable);

				originalNode = appendNode(parentNode, addedNode);

				// 超過部分加班分估取後一段
				nextNewNode = getNextNewNode(addedNode, nextHours, nextHoursTaxfree, nextHoursTaxable);
				originalNode = combineNodes(originalNode, nextNewNode);
			    }
			} else if (parentHours.doubleValue() == ruleHours.doubleValue()) {
			    // 父節點加班時長等於分段時長=直接在當前節點上增加後續節點，加班分段要取後一段
			    nextNewNode = getNextNewNode(addedNode, addedNode.getNodeData().getHours(), addedNode
				    .getNodeData().getHourstaxfree(), addedNode.getNodeData().getHourstaxable());
			    originalNode = combineNodes(originalNode, nextNewNode);
			}
		    } else {
			// 節點編碼不相同，直接在當前節點上增加後續節點
			originalNode = appendNode(parentNode, addedNode);
		    }
		}
	    } while ((curNode.getNextNode() != null));
	}

	return OTSChainUtils.getFirstNode(originalNode);
    }

    private static OTSChainNode getNextNewNode(OTSChainNode newNode, UFDouble nextHours, UFDouble nextHoursTaxfree,
	    UFDouble nextHoursTaxable) throws BusinessException {
	OTSChainNode nextNewNode;
	nextNewNode = newNode.cloneSingle();
	nextNewNode.setNextNode(null);
	nextNewNode.setPriorNode(null);
	nextNewNode.getNodeData().setHours(nextHours);
	nextNewNode.getNodeData().setHourstaxfree(nextHoursTaxfree);
	nextNewNode.getNodeData().setHourstaxable(nextHoursTaxable);
	nextNewNode.setNodeData(getNextSegRuleTerm(nextNewNode.getNodeData()));
	return nextNewNode;
    }

    @SuppressWarnings("unchecked")
    private static SegDetailVO getNextSegRuleTerm(SegDetailVO nodeData) throws BusinessException {
	SegDetailVO ret = nodeData;
	Collection<SegRuleTermVO> terms = getBaseDAO().retrieveByClause(SegRuleTermVO.class,
		"pk_segrule='" + nodeData.getPk_segrule() + "' and dr=0", "segno");

	if (terms == null || terms.size() == 0) {
	    throw new BusinessException("創建新後續節點失敗：未找到已設定的分段規則明細");
	} else {
	    int count = 0;
	    boolean getThis = false;
	    SegRuleTermVO nextTerm = null;
	    for (SegRuleTermVO term : terms) {
		if (getThis) {
		    nextTerm = term;
		    break;
		}
		count++;
		if (term.getPk_segruleterm().equals(nodeData.getPk_segruleterm())) {
		    if (count == terms.size()) {
			nextTerm = term;
		    } else {
			getThis = true;
		    }
		}
	    }

	    UFDouble start = nextTerm.getStartpoint();
	    UFDouble end = nextTerm.getEndpoint() == null ? new UFDouble(24) : nextTerm.getEndpoint();
	    UFDouble taxablerate = nextTerm.getTaxableotrate();
	    UFDouble taxfreerate = nextTerm.getTaxfreeotrate();
	    ret.setPk_segruleterm(nextTerm.getPk_segruleterm());
	    ret.setRulehours(end.sub(start));
	    ret.setTaxfreerate(taxfreerate);
	    ret.setTaxablerate(taxablerate);
	}
	return ret;
    }

    /**
     * 取所有上線節點Code完全一樣的各級父節點加班時數之和
     * 
     * @param currentNode
     * @return
     */
    private static UFDouble getParentTotalHoursBySameCode(OTSChainNode currentNode) {
	UFDouble hours = currentNode.getNodeData().getHours();
	OTSChainNode curNode = currentNode;
	// 上級節點不為空，且上級節點的節點編碼=本次纍加的節點編碼
	while (curNode.getPriorNode() != null
		&& curNode.getPriorNode().getNodeData().getNodecode().equals(currentNode.getNodeData().getNodecode())) {
	    hours = hours.add(curNode.getPriorNode().getNodeData().getHours());
	    curNode = curNode.getPriorNode();
	}
	return hours;
    }

    /**
     * 查找父節點
     * 
     * @param originalNode
     *            原始節點鏈表
     * @param checkNode
     *            檢查節點
     * @return
     * @throws BusinessException
     */
    public static OTSChainNode findParentNode(OTSChainNode originalNode, OTSChainNode checkNode)
	    throws BusinessException {
	if (originalNode == null) {
	    return null;
	}
	String checkedNodeCode = checkNode.getNodeData().getNodecode();
	String[] checkedCodeList = checkedNodeCode.split(SPLT);
	OTSChainNode retNode = null;
	OTSChainNode curNode = getLastNode(originalNode);
	do {
	    // 向上尋找第一個NodeCode相同或者早於檢查節點
	    String curNodeCode = StringUtils.isEmpty(curNode.getNodeData().getNodecode()) ? "" : curNode.getNodeData()
		    .getNodecode();
	    String[] curCodeList = curNodeCode.split(SPLT);
	    if (curNodeCode.equals(checkedNodeCode)
		    || (curCodeList[0].equals(checkedCodeList[0]) && (curCodeList[1] + curCodeList[3])
			    .compareTo(checkedCodeList[1] + checkedCodeList[3]) <= 0)) {
		retNode = curNode;
		break;
	    }
	    curNode = curNode.getPriorNode();
	} while (curNode.getPriorNode() != null);
	return retNode;
    }

    /**
     * 取所有節點的加班分段明細
     * 
     * @param node
     *            節點
     * @return
     * @throws BusinessException
     */
    public static SegDetailVO[] getAllNodeData(OTSChainNode node) throws BusinessException {
	List<SegDetailVO> segDetailVOs = new ArrayList<SegDetailVO>();

	OTSChainNode curNode = getFirstNode(node);
	do {
	    segDetailVOs.add(curNode.getNodeData());
	    curNode = curNode.getNextNode();
	} while (curNode != null);

	return segDetailVOs.toArray(new SegDetailVO[0]);
    }
}
