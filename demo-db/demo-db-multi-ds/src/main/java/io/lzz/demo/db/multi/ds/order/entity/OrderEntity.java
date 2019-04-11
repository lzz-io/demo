/*
 * Copyright qq:1219331697
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.lzz.demo.db.multi.ds.order.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author q1219331697
 *
 */
@Entity
@Table(name = "tb_order")
public class OrderEntity implements Serializable {

	private static final long serialVersionUID = 3858642238969567042L;

	@Id
	@GeneratedValue
	private Long id;

	private Long userId;

	private String remark;

	public OrderEntity() {
	}

	public OrderEntity(Long id, Long userId, String remark) {
		this.id = id;
		this.userId = userId;
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", userId=" + userId + ", remark=" + remark + "]";
	}

}
