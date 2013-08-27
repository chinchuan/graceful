package org.graceful.wr.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.graceful.correct.core.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {
	
	@RequestMapping("/ajax")
	public String test(Model model) {
		Page<TestModel> paging = new Page<TestModel>();
		paging.setPageSize(20);
		paging.setCurrentPage(1);
		paging.setRecordCount(1000);
		
		List<TestModel> list = new ArrayList<TestModel>(20);
		for(int i=0;i<paging.getPageSize();i++) {
			
			TestModel m = new TestModel();
			m.name = "name"+i;
			m.value  = "value" +i;
			m.setOrder(String.valueOf(i));
			m.setIndex(String.valueOf(i));
			list.add(m);
		}
		paging.setElements(list);
		model.addAttribute("page", paging);
		 
		return "test";
	}
	
	private  class TestModel implements Serializable{
		private String name;
		private String value;
		private String index;
		private String order;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
		}
		public String getOrder() {
			return order;
		}
		public void setOrder(String order) {
			this.order = order;
		}
		
	}
}
