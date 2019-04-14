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

package io.lzz.demo.db.multi.ds.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author q1219331697
 *
 */
@Aspect
@Component
@Order(-1) // 保证该AOP在@Transactional之前执行
public class DynamicDataSourceAop {

	private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceAop.class);

	@Pointcut("execution(* io.lzz..*.*Service.*(..))")
	public void pointcut() {
	}

	@Before(value = "io.lzz.demo.db.multi.ds.config.DynamicDataSourceAop.pointcut()")
	public void before(JoinPoint joinPoint) {
		log.info("before {}", joinPoint);
	}

	@Around(value = "io.lzz.demo.db.multi.ds.config.DynamicDataSourceAop.pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("-------------- around {}", joinPoint);
		String packageName = joinPoint.getTarget().getClass().getName();
		// boolean match = StringUtils.substringMatch(packageName, 0, "order");
		String dataSourceKey = DynamicDataSourceHolder.getDataSourceKey();

		Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)Service");
		Matcher matcher = pattern.matcher(packageName);
		if (matcher.find()) {
			String group = matcher.group(1);
			group = StringUtils.uncapitalize(group);
			dataSourceKey = group + "DataSource";
		}
		log.info("dataSourceKey={}", dataSourceKey);
		DynamicDataSourceHolder.setDataSourceKey(dataSourceKey);

		try {
			return joinPoint.proceed();
		} finally {
			// DynamicDataSource.clearDataSource();
			// 将数据源更改为进入切入点之前的数据源。当然，如果进入切入点之前没有任何数据源，则此时的oldDataSourceName为null
			// 这样做的好处是，如果一个方法A内部调用了多个配置其他数据源的方法时，不会影响方法A上配置的数据源。
			// 但允许方法A内部的方法上的数据源配置，可以覆盖方法A上的数据源配置
			if (dataSourceKey == null) {
				DynamicDataSourceHolder.removeDataSourceKey();
			} else {
				DynamicDataSourceHolder.setDataSourceKey(dataSourceKey);
			}
		}

	}

	@After(value = "io.lzz.demo.db.multi.ds.config.DynamicDataSourceAop.pointcut()")
	public void after(JoinPoint joinPoint) {
		log.info("after {}", joinPoint);
	}

}
