package operato.fnf.wcs.service.board;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import operato.fnf.wcs.FnfUtils;
import xyz.anythings.base.model.ResponseObj;
import xyz.anythings.base.service.impl.AbstractLogisService;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.orm.manager.DataSourceManager;
import xyz.elidom.util.BeanUtil;
import xyz.elidom.util.DateUtil;
import xyz.elidom.util.ValueUtil;

@Component
public class GetWmsDpsSummary extends AbstractLogisService {
	public ResponseObj getWmsDpsSummary(Map<String, Object> params) throws Exception {
		String date = String.valueOf(params.get("date"));
		if (ValueUtil.isEmpty(date)) {
			params.put("date", DateUtil.getCurrentDay());
		}
		
		List<String> brands = null;
		if (ValueUtil.isNotEmpty(params.get("brand"))) {
			brands = Arrays.asList(String.valueOf(params.get("brand")).split(","));
			params.put("brand", brands);
		}
		
		String wmsSql = FnfUtils.queryCustServiceWithCheck("board_wms_dps_summary");
		IQueryManager wmsQueryMgr = BeanUtil.get(DataSourceManager.class).getQueryManager("WMS");
		@SuppressWarnings("unchecked")
		Map<String, Object> wmsDpsSum = (Map<String, Object>) wmsQueryMgr.selectBySql(wmsSql, params, HashMap.class);
		if (ValueUtil.isEmpty(wmsDpsSum)) {
			return new ResponseObj();
		}
		
		Integer donePcsQty = Integer.parseInt(String.valueOf(wmsDpsSum.get("done_pcs_qty")));
		Integer totalPcsQty = Integer.parseInt(String.valueOf(wmsDpsSum.get("total_pcs_qty")));
		Integer doneOrderCnt = Integer.parseInt(String.valueOf(wmsDpsSum.get("done_order_cnt")));
		Integer totalOrderCnt = Integer.parseInt(String.valueOf(wmsDpsSum.get("total_order_cnt")));
		Integer doneShipIdCnt = Integer.parseInt(String.valueOf(wmsDpsSum.get("done_shipid_cnt")));
		Integer totalShipIdCnt = Integer.parseInt(String.valueOf(wmsDpsSum.get("total_shipid_cnt")));
		Integer doneSkuCnt = Integer.parseInt(String.valueOf(wmsDpsSum.get("done_sku_cnt")));
		Integer totalSkuCnt = Integer.parseInt(String.valueOf(wmsDpsSum.get("total_sku_cnt")));
		
		Map<String, Object> result = new HashMap<>();
		result.put("done_pcs_qty", donePcsQty);
		result.put("total_pcs_qty", totalPcsQty);
		result.put("done_order_qty", doneOrderCnt);
		result.put("total_order_qty", totalOrderCnt);
		result.put("done_shipid_cnt", doneShipIdCnt);
		result.put("total_shipid_cnt", totalShipIdCnt);
		result.put("done_sku_cnt", doneSkuCnt);
		result.put("total_sku_cnt", totalSkuCnt);
		
		// WCS 합포수량 조회
		String wcsSql = FnfUtils.queryCustServiceWithCheck("board_wcs_dps_summary");
		@SuppressWarnings("unchecked")
		Map<String, Object> wcsDpsSum = (Map<String, Object>) queryManager.selectBySql(wcsSql, params, HashMap.class);
		Float hDoneOrderCnt = 0f;
		Float hTotalOrderCnt = 0f; 
		if (ValueUtil.isEmpty(wcsDpsSum)) {
			hDoneOrderCnt = Float.parseFloat(String.valueOf(wmsDpsSum.get("h_done_order_cnt")));
			hTotalOrderCnt = Float.parseFloat(String.valueOf(wmsDpsSum.get("h_total_order_cnt")));
		} else {
			hDoneOrderCnt = Float.parseFloat(String.valueOf(wcsDpsSum.get("h_done_order_cnt")));
			hTotalOrderCnt = Float.parseFloat(String.valueOf(wcsDpsSum.get("h_total_order_cnt")));
		}
		
		// WMS 전체수량 - WCS 합포수량 = 단포수량 
//		Integer dDonePcsQty = donePcsQty - hDonePcsQty;
//		Integer dOrderPcsQty = doneOrderCnt - hOrderPcsQty;
		Float dDoneOrderCnt = doneOrderCnt - hDoneOrderCnt;
		Float dTotalOrderCnt = totalOrderCnt - hTotalOrderCnt;
		
		
		result.put("multi_done_ord_cnt", hDoneOrderCnt);
		result.put("multi_ord_cnt", hTotalOrderCnt);
		result.put("single_done_ord_cnt", dDoneOrderCnt);
		result.put("single_ord_cnt", dTotalOrderCnt);
		
		if (ValueUtil.isNotEmpty(totalOrderCnt) && totalOrderCnt > 0) {			
			result.put("done_order_rate", doneOrderCnt/totalOrderCnt * 100);
		}
		if (ValueUtil.isNotEmpty(totalPcsQty) && totalPcsQty > 0) {
			result.put("done_pcs_rate", donePcsQty/totalPcsQty * 100);
		}
		if (ValueUtil.isNotEmpty(hTotalOrderCnt) && hTotalOrderCnt > 0) {
			result.put("multi_done_ord_rate", hDoneOrderCnt/hTotalOrderCnt * 100);
		}
		if (ValueUtil.isNotEmpty(dTotalOrderCnt) && dTotalOrderCnt > 0) {
			result.put("single_done_ord_rate", dDoneOrderCnt/dTotalOrderCnt * 100);
		}
		
		ResponseObj resp = new ResponseObj();
		resp.setValues(result);
		return resp;
	}
}
