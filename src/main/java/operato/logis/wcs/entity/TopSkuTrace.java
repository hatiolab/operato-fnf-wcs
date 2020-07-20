package operato.logis.wcs.entity;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Table;

@Table(name = "top_sku_trace", idStrategy = GenerationRule.UUID)
public class TopSkuTrace extends xyz.elidom.orm.entity.basic.ElidomStampHook {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 514587061719080963L;

	@PrimaryKey
	@Column (name = "id", nullable = false, length = 40)
	private String id;

	@Column (name = "work_type", length = 16)
	private String workType;

	@Column (name = "sum_date", length = 16)
	private String sumDate;

	@Column (name = "scope_days")
	private Integer scopeDays;

	@Column (name = "outb_days_rate")
	private Float outbDaysRate;

	@Column (name = "outb_count_rate")
	private Float outbCountRate;

	@Column (name = "duration_days")
	private Integer durationDays;

	@Column (name = "sku_cd", length = 64)
	private String skuCd;

	@Column (name = "scope_days_pcs_qty")
	private Integer scopeDaysPcsQty;

	@Column (name = "scope_days_ord_cnt")
	private Integer scopeDaysOrdCnt;

	@Column (name = "scope_days_sku_cnt")
	private Integer scopeDaysSkuCnt;

	@Column (name = "scope_avg_pcs_qty")
	private Integer scopeAvgPcsQty;

	@Column (name = "popular_index")
	private Float popularIndex;
  
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getSumDate() {
		return sumDate;
	}

	public void setSumDate(String sumDate) {
		this.sumDate = sumDate;
	}

	public Integer getScopeDays() {
		return scopeDays;
	}

	public void setScopeDays(Integer scopeDays) {
		this.scopeDays = scopeDays;
	}

	public Float getOutbDaysRate() {
		return outbDaysRate;
	}

	public void setOutbDaysRate(Float outbDaysRate) {
		this.outbDaysRate = outbDaysRate;
	}

	public Float getOutbCountRate() {
		return outbCountRate;
	}

	public void setOutbCountRate(Float outbCountRate) {
		this.outbCountRate = outbCountRate;
	}

	public Integer getDurationDays() {
		return durationDays;
	}

	public void setDurationDays(Integer durationDays) {
		this.durationDays = durationDays;
	}

	public String getSkuCd() {
		return skuCd;
	}

	public void setSkuCd(String skuCd) {
		this.skuCd = skuCd;
	}

	public Integer getScopeDaysPcsQty() {
		return scopeDaysPcsQty;
	}

	public void setScopeDaysPcsQty(Integer scopeDaysPcsQty) {
		this.scopeDaysPcsQty = scopeDaysPcsQty;
	}

	public Integer getScopeDaysOrdCnt() {
		return scopeDaysOrdCnt;
	}

	public void setScopeDaysOrdCnt(Integer scopeDaysOrdCnt) {
		this.scopeDaysOrdCnt = scopeDaysOrdCnt;
	}

	public Integer getScopeDaysSkuCnt() {
		return scopeDaysSkuCnt;
	}

	public void setScopeDaysSkuCnt(Integer scopeDaysSkuCnt) {
		this.scopeDaysSkuCnt = scopeDaysSkuCnt;
	}

	public Integer getScopeAvgPcsQty() {
		return scopeAvgPcsQty;
	}

	public void setScopeAvgPcsQty(Integer scopeAvgPcsQty) {
		this.scopeAvgPcsQty = scopeAvgPcsQty;
	}

	public Float getPopularIndex() {
		return popularIndex;
	}

	public void setPopularIndex(Float popularIndex) {
		this.popularIndex = popularIndex;
	}	
}
