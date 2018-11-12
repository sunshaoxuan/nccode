package nc.ui.ta.psncalendar.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.framework.common.NCLocator;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.ui.pub.bill.BillSortListener;
import nc.ui.pub.bill.IBillRelaSortListener;
import nc.ui.ta.calendar.pub.CalendarAppEventConst;
import nc.ui.ta.pub.model.IDoCancelAppModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

public class PsnCalendarAppModel extends AbstractUIAppModel implements BillSortListener,IBillRelaSortListener,IDoCancelAppModel{
	
//	PsnJobCalendarVO selectedVO;
	PsnJobCalendarVO[] data;
	int selectedRow;
	//��¼�кŵ�map��key��pk_psndoc��value���к�
	Map<String, Integer> indexMap;
	//������ʱ���õ�list��ÿ������󣬴�list�е����ݶ��Ƿ���˳��ģ���getRelaSortObject()����
	List<PsnJobCalendarVO> sortList = new ArrayList<PsnJobCalendarVO>();
	
	UFLiteralDate selectedDate;
	UFLiteralDate beginDate;
	UFLiteralDate endDate;
	
	private String selectedPsnName;

	public PsnCalendarAppModel() {
		indexMap = new HashMap<String, Integer>();
	}

	@Override
	public Object getSelectedData() {
		return getSelectedVO();
	}
	
	public PsnJobCalendarVO getSelectedVO(){
		if(selectedRow<0||ArrayUtils.isEmpty(data))
			return null;
		if(selectedRow>=data.length)
			return null;
		return data[selectedRow];
	}
	
	public void setSelectedVO(PsnJobCalendarVO selectedVO){
		if(selectedVO!=null){
			selectedRow = indexMap.get(selectedVO.getPk_psndoc());
		}
		fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED,this,null));
	}

	@Override
	public void initModel(Object data) {
		this.data=(PsnJobCalendarVO[])data;
		setSelectedDate(null);
		fireEvent(new AppEvent(AppEventConst.MODEL_INITIALIZED,this,null));
		int count = this.data==null?0:this.data.length;
		setSelectedRow(count>0?0:-1);
		indexMap.clear();
		if(!ArrayUtils.isEmpty(this.data)){
			for(int i=0;i<this.data.length;i++){
				indexMap.put(this.data[i].getPk_psndoc(), i);
			}
		}
	}
	public void setSelectedRow(int selectedRow) {
		if(this.selectedRow == selectedRow)
			return;
		this.selectedRow = selectedRow;
		fireEvent(new AppEvent(AppEventConst.SELECTION_CHANGED,this,null));
	}
	
	public PsnJobCalendarVO[] getData(){
		return data;
	}

	public int getSelectedRow() {
		return selectedRow;
	}
	
	/**
	 * ��������ϵ�����
	 * @param saveData
	 * @return
	 * @throws BusinessException
	 */
	public PsnJobCalendarVO[] save(PsnJobCalendarVO[] saveData) throws BusinessException{
		if(ArrayUtils.isEmpty(saveData))
			return null;
		IPsnCalendarManageMaintain manageMaintain = NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
		saveData = manageMaintain.save(getContext().getPk_org(), saveData);
		directlyUpdate(saveData);
		return saveData;
	}
	
	/**
	 * ����model�����ݣ������Ӻ�̨
	 * @param updateData
	 */
	public void directlyUpdate(PsnJobCalendarVO[] updateData){
		if(ArrayUtils.isEmpty(updateData))
			return;
		List<Integer> updateIndexList = new ArrayList<Integer>();
		//��data�е�����update
		for(int i=0;i<updateData.length;i++){
			int index = indexMap.get(updateData[i].getPk_psndoc());
			this.data[index]= updateData[i];
			updateIndexList.add(index);
		}
		RowOperationInfo rowOpInfo = new RowOperationInfo(updateIndexList, updateData);
		fireEvent(new AppEvent(AppEventConst.DATA_UPDATED, this, rowOpInfo));
	}

	@Override
	public void afterSort(String key) {
		if(ArrayUtils.isEmpty(data))
			return;
		//��data����Ϊ��sortList�е�˳��һ�£��������µ�selectedrow
		//������index map��key����Ա����pk_psndoc��value�����
		Map<String, Integer> newIndexMap = new HashMap<String, Integer>();
		for(int i=0;i<sortList.size();i++){
			newIndexMap.put(sortList.get(i).getPk_psndoc(), i);
		}
		//�����µ�ѡ����
		if(selectedRow>=0)
			selectedRow = newIndexMap.get(data[selectedRow].getPk_psndoc());
		//��data�е�˳�����õ���sortlist��һ��
		for(int i=0;i<data.length;i++){
			data[i]=sortList.get(i);
		}
		//����indexMap
		indexMap.clear();
		indexMap.putAll(newIndexMap);
		fireEvent(new AppEvent(CalendarAppEventConst.MODEL_SORTED, this, null));
	}

	/* 
	 * model�������������¼�������ʱ����ô˷��������ú�list�е������Ƿ��Ͻ����˳���
	 * (non-Javadoc)
	 * @see nc.ui.pub.bill.IBillRelaSortListener#getRelaSortObject()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List getRelaSortObject() {
		sortList.clear();
		if(ArrayUtils.isEmpty(data))
			return sortList;
		sortList.addAll(Arrays.asList(data));
		return sortList;
	}
	
	public void setBeginEndDate(UFLiteralDate beginDate,UFLiteralDate endDate){
//		if(beginDate.equals(this.beginDate)&&endDate.equals(this.endDate))
//			return;
		this.beginDate=beginDate;
		this.endDate=endDate;
		fireEvent(new AppEvent(CalendarAppEventConst.DATE_CHANGED, this, null));
	}

	public UFLiteralDate getBeginDate() {
		return beginDate;
	}

	public UFLiteralDate getEndDate() {
		return endDate;
	}
	
	public UFLiteralDate getSelectedDate() {
		return selectedDate;
	}
	public void setSelectedDate(UFLiteralDate selectedDate) {
		this.selectedDate = selectedDate;
	}

	public String getSelectedPsnName() {
		return selectedPsnName;
	}

	public void setSelectedPsnName(String selectedPsnName) {
		this.selectedPsnName = selectedPsnName;
	}
	@Override
	public void doCancel(){
		fireEvent(new AppEvent(CalendarAppEventConst.EDIT_CANCELED, this, null));
		if(!ArrayUtils.isEmpty(data)){
			for(PsnJobCalendarVO vo:data){
				vo.getModifiedCalendarMap().clear();
			}
		}
		setUiState(UIState.NOT_EDIT);
	}
	
	public void buChanged(){
		fireEvent(new AppEvent(CalendarAppEventConst.BU_CHANGED, this, null));
	}

	public PsnJobCalendarVO[] insert(PsnJobCalendarVO[] saveData) throws BusinessException {
		
		if(ArrayUtils.isEmpty(saveData))
			return null;
		IPsnCalendarManageMaintain manageMaintain = NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
		saveData = manageMaintain.save(getContext().getPk_org(), saveData);
		directlyUpdate(saveData);
		return saveData;
		
	}
}