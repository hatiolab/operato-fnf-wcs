package operato.fnf.wcs.query.store;

import org.springframework.stereotype.Component;

import xyz.anythings.sys.service.AbstractQueryStore;
import xyz.elidom.sys.SysConstants;

/**
 * FnF DPS 용 쿼리 스토어
 * 
 * @author shortstop
 */
@Component
public class FnFDpsQueryStore extends AbstractQueryStore {

	@Override
	public void initQueryStore(String databaseType) {
		this.databaseType = databaseType;
		this.basePath = "operato/fnf/wcs/query/" + this.databaseType + SysConstants.SLASH;
		this.defaultBasePath = "operato/fnf/wcs/query/ansi/";
	}

	/**
	 * 작업 배치별 최종 10분대 별 작업 실적 서머리 조회
	 * 
	 * @return
	 */
	public String getDpsBatchTotalResultSummary() {
		return this.getQueryByPath("dps/BatchTotalMinuteResultSummary");
	}
	
	/**
	 * 작업 배치별 10분대 별 작업 실적 서머리 조회
	 * 
	 * @return
	 */
	public String getDpsCalc10MinuteResultSummary() {
		return this.getQueryByPath("dps/Calc10MinuteResultSummary");
	}
	
	/**
	 * 작업 배치별 1시간별 작업 실적 서머리 조회
	 * 
	 * @return
	 */
	public String getDpsCalc1HourResultSummary() {
		return this.getQueryByPath("dps/Calc1HourResultSummary");
	}
	
	/**
	 * 작업 배치별 설비 중단 시간
	 * 
	 * @return
	 */
	public String getDpsEquipmentIdleTime() {
		return this.getQueryByPath("dps/EquipmentIdleTime");
	}
	
	/**
	 * 작업 할당을 위한 재고 조회 
	 * 
	 * @return
	 */
	public String getStocksForJobAssign() {
		return this.getQueryByPath("dps/StocksForJobAssign");
	}
	
	/**
	 * 해당 재고가 필요한 주문 조회
	 * 
	 * @return
	 */
	public String getSearchOrderForStock() {
		return this.getQueryByPath("dps/SearchOrdersForStock");
	}
	
	/**
	 * 작업 할당에 필요한 주문 및 재고 조합 정보 조회
	 * 
	 * @return
	 */
	public String getSearchAssignCandidates() {
		return this.getQueryByPath("dps/SearchAssignCandidates");
	}
	
	/**
	 * 경광등 빈 셀 상태 조회 
	 * 
	 * @return
	 */
	public String getTowerLampStatus() {
		return this.getQueryByPath("dps/TowerLampStatus");
	}

}
