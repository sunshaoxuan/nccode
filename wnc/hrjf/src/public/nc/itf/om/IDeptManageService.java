package nc.itf.om;

import java.util.HashMap;

import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptCopyWrapperVO;
import nc.vo.om.hrdept.DeptHistoryVO;
import nc.vo.om.hrdept.DeptMergeWrapperVO;
import nc.vo.om.hrdept.DeptTransDeptVO;
import nc.vo.om.hrdept.DeptTransItemVO;
import nc.vo.om.hrdept.DeptTransRefNameVO;
import nc.vo.om.hrdept.DeptTransRuleVO;
import nc.vo.om.hrdept.PostAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;

public interface IDeptManageService
{
    
    /**
     * ɾ��<br>
     * 
     * @param vo
     */
    void delete(AggHRDeptVO vo) throws BusinessException;
    
    /**
     * ����<br>
     * 
     * @param vo
     * @return
     */
    AggHRDeptVO insert(AggHRDeptVO vo) throws BusinessException;
    
    /**
     * �޸�<br>
     * 
     * @param vo
     * @return
     */
    AggHRDeptVO update(AggHRDeptVO vo, boolean blChangeAudltInfo) throws BusinessException;
    
    /**
     * ��ȡ���������빤��ʹ��
     * @param vo
     * @param blChangeAudltInfo
     * @return
     * @throws BusinessException
     */
    AggHRDeptVO updateImportVO(AggHRDeptVO vo, boolean blChangeAudltInfo) throws BusinessException;
    
    /**
     * �������Žṹ�°汾<br>
     * 
     * @param pk_org
     * @param vname
     * @param vstartdate
     * @throws BusinessException
     */
    public void createDeptStruVersion(String pk_org, String vname, UFDate vstartdate) throws BusinessException;
    
    /**
     * �������������°汾
     * <p>
     * ���ڲ��ŵı����������Ҫͬʱ�汾���ܶಿ�ŵĳ���
     * 
     * @param deptVO
     * @throws BusinessException
     */
    public AggHRDeptVO[] createDeptVersion(AggHRDeptVO[] deptVOs) throws BusinessException;
    
    /**
     * ���������°汾<br>
     * 
     * @param deptVO
     * @throws BusinessException
     */
    public AggHRDeptVO createDeptVersion(AggHRDeptVO deptVO) throws BusinessException;
    
    /**
     * �Բ��Ž��и���<br>
     * 
     * @param wrapperVO
     * @return
     * @throws BusinessException
     */
    void copy(DeptCopyWrapperVO wrapperVO) throws BusinessException;
    
    /**
     * ���Ÿ���<br>
     * 
     * @param deptVO ����VO
     * @param updateCareer �Ƿ�ͬ��������������
     * @return
     * @throws BusinessException
     */
    AggHRDeptVO rename(AggHRDeptVO deptVO, DeptHistoryVO historyVO, boolean updateCareer) throws BusinessException;
    /**
     * ���Ÿ���<br>
     * 
     * @param deptVO ����VO
     * @param updateCareer �Ƿ�ͬ��������������
     * @param changeType ׃�����:1.ֻ���� 2.ֻ׃��ؓ؟�� 3.������ؓ؟�˶��M��
     * @return
     * @throws BusinessException
     */
    AggHRDeptVO renameAndPrincipalChange(AggHRDeptVO deptVO, DeptHistoryVO historyVO, boolean updateCareer,int changeType) throws BusinessException;
    
    /**
     * ����ת��<br>
     * 
     * @param deptVO
     * @param historyVO
     * @return ��ת�Ʋ��źͽ��ղ���
     * @throws BusinessException
     */
    AggHRDeptVO[] shift(AggHRDeptVO deptVO, DeptHistoryVO historyVO) throws BusinessException;
    
    /**
     * Ϊ���źϲ��ĵڶ��������λ��Ϣ<br>
     * 
     * @throws BusinessException
     */
    PostAdjustVO[] savePostForDeptMerge(PostAdjustVO[] alreadySavedPostVOs, PostAdjustVO[] backupPostVOs, PostAdjustVO[] postAdjustVOs)
            throws BusinessException;
    
    /**
     * ��ѡ��Ĳ��ŷ����仯ʱ�ع����ݿ���ĸ�λ��Ϣ<br>
     * 
     * @throws BusinessException
     */
    void rollbackPostForDeptMerge(PostAdjustVO[] savedPostVOs, PostAdjustVO[] backupPostVOs) throws BusinessException;
    
    /**
     * ���źϲ�<br>
     * 
     * @param wrapperVO
     * @throws BusinessException
     */
    AggHRDeptVO[] merge(DeptMergeWrapperVO wrapperVO) throws BusinessException;
    
    /**
     * ���ų���<br>
     * 
     * @param vo
     * @return
     * @throws BusinessException
     */
    AggHRDeptVO[] cancel(AggHRDeptVO vo, DeptHistoryVO historyVO, boolean disableDept) throws BusinessException;
    
    /**
     * ���ŷ�����<br>
     * 
     * @param vo
     * @param historyVO
     * @param includeChildDept
     * @param includePost
     * @return
     * @throws BusinessException
     */
    AggHRDeptVO[] uncancel(AggHRDeptVO vo, DeptHistoryVO historyVO, boolean includeChildDept, boolean includePost, boolean enableDept)
            throws BusinessException;
    
    /**
     * ִ�п�ҵ��Ԫ����ת��
     * 
     * @param ruleVO
     * @param refNameVOs
     * @param transDeptVOs
     * @param transItemVOs
     * @param transPsnInfVOs
     * @return
     * @throws BusinessException
     */
    public void doTransDeptInf(DeptTransRuleVO ruleVO, DeptTransRefNameVO[] refNameVOs, DeptTransDeptVO[] transDeptVOs,
            DeptTransItemVO[] transItemVOs, GeneralVO[] transPsnInfVOs) throws BusinessException;
    
    /**
     * ���빤�߲��룬��������Ϣ<br>
     * 
     * @param vo
     * @return
     * @throws BusinessException
     */
    public AggHRDeptVO insertImportVO(AggHRDeptVO vo) throws BusinessException;
    
    /**
     * ���빤������ʱ���¼���֪ͨ
     * 
     * @param vo
     * @throws BusinessException
     */
    public void fireAfterImportInsertEvent(AggHRDeptVO[] vos) throws BusinessException;
    
    /**
     * ���� <br>
     * @param updateVO
     * @param strFieldCode
     * @param strPk_depts
     * @return AggHRDeptVO
     * @throws BusinessException
     */
    AggHRDeptVO[] batchUpdateDeptMain(SuperVO updateVO, String strFieldCode, String[] strPk_depts) throws BusinessException;
    
    /**
     * ��������
     * @param deptMap ����aggvo�Բ��ű����ʷӳ��
     * @param updateCareer �Ƿ�ͬ������
     * @param needCreateVersion �Ƿ�汾��
     * @param versionMemo �°汾˵��
     * @return �����������VO�԰汾����ʾ��Ϣ��ӳ��
     * @throws BusinessException
     */
    public HashMap<AggHRDeptVO, String> rename(HashMap<AggHRDeptVO, DeptHistoryVO> deptMap, boolean updateCareer,
            boolean needCreateVersion, String versionMemo) throws BusinessException;
    
    /**
     * ��������
     * @param deptMap ����aggvo�Բ��ű����ʷӳ��
     * @param disableDept �Ƿ�ͣ��
     * @param needCreateVersion �Ƿ�汾��
     * @param versionMemo �°汾˵��
     * @return �����������VO�԰汾����ʾ��Ϣ��ӳ��,�����¼�����
     * @throws BusinessException
     */
    public HashMap<AggHRDeptVO, String> cancel(HashMap<AggHRDeptVO, DeptHistoryVO> deptMap, boolean disableDept, boolean needCreateVersion,
            String versionMemo) throws BusinessException;
    
    /**
     * ����������
     * @param deptMap ����aggvo�Բ��ű����ʷӳ��
     * @param includeChildDept �Ƿ�����¼�����
     * @param includePost �Ƿ������λ
     * @param enableDept �Ƿ�����
     * @param needCreateVersion �Ƿ�汾��
     * @param versionMemo �°汾˵��
     * @return �������������VO�԰汾����ʾ��Ϣ��ӳ��
     * @throws BusinessException
     */
    public HashMap<AggHRDeptVO, String> uncancel(HashMap<AggHRDeptVO, DeptHistoryVO> deptMap, boolean includeChildDept,
            boolean includePost, boolean enableDept, boolean needCreateVersion, String versionMemo) throws BusinessException;
    /**
     * ���������°汾<br>
     * 
     * @param deptVO
     * @param vstartdate ��Чʱ��
     * @throws BusinessException
     */
    public AggHRDeptVO createDeptVersion(AggHRDeptVO deptVO,UFDate vstartdate) throws BusinessException;
    
    /**
     * �������������°汾
     * <p>
     * ���ڲ��ŵı����������Ҫͬʱ�汾���ܶಿ�ŵĳ���
     * 
     * @param deptVO
     * @throws BusinessException
     */
    public AggHRDeptVO[] createDeptVersion(AggHRDeptVO[] deptVOs,UFDate vstartdate) throws BusinessException;
}