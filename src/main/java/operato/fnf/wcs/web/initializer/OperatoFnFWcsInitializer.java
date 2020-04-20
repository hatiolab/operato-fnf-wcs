/* Copyright © HatioLab Inc. All rights reserved. */
package operato.fnf.wcs.web.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import operato.fnf.wcs.config.ModuleProperties;
import xyz.elidom.sys.config.ModuleConfigSet;
import xyz.elidom.sys.system.service.api.IEntityFieldCache;
import xyz.elidom.sys.system.service.api.IServiceFinder;

/**
 * FnF WCS 모듈 Startup시 Framework 초기화 클래스
 * 
 * @author yang
 */
@Component
public class OperatoFnFWcsInitializer {

	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(OperatoFnFWcsInitializer.class);

	@Autowired
	@Qualifier("rest")
	private IServiceFinder restFinder;

	@Autowired
	private IEntityFieldCache entityFieldCache;

	@Autowired
	private ModuleProperties module;

	@Autowired
	private ModuleConfigSet configSet;
	
	@EventListener({ ContextRefreshedEvent.class })
	public void refresh(ContextRefreshedEvent event) {
		this.logger.info("FnF WCS module refreshing...");
		
		this.configSet.addConfig(this.module.getName(), this.module);
		this.configSet.setApplicationModule(this.module.getName());
		this.scanServices();
		
		this.logger.info("FnF WCS module refreshed!");
	}

	@EventListener({ ApplicationReadyEvent.class })
	void ready(ApplicationReadyEvent event) {
		this.logger.info("FnF WCS module initializing...");
		
		this.logger.info("FnF WCS module initialized!");
	}

	/**
	 * 모듈 서비스 스캔
	 */
	private void scanServices() {
		this.entityFieldCache.scanEntityFieldsByBasePackage(this.module.getBasePackage());
		this.restFinder.scanServicesByPackage(this.module.getName(), this.module.getBasePackage());
	}
}