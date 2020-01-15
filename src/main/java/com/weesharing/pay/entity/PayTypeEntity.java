package com.weesharing.pay.entity;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.weesharing.pay.dto.paytype.MutilPayType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wspay_paytype")
@ApiModel(value="支付方式对象", description="")
public class PayTypeEntity extends Model<PayTypeEntity>{
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

	@NotNull(message = "appid不能为空")
	private String appid;
	
	@NotNull(message = "支付方式不能为空")
	private String name;
	
	private String description;
	
	public MutilPayType convert() {
		MutilPayType entity = new MutilPayType();
		entity.setId(this.id);
		entity.setAppid(this.appid);
		entity.setName(this.name);
		entity.setDesc(this.description);
		return entity;
	} 

}
