// JavaScript Document
;(function($,window,undefined){
		   
var wrGrid =  function(grid,config){
	config = $.extend(wrGrid.defaults,config);
	
	var id = $(grid).attr('id') 
	if(!id) {
		id =  wrGrid.uuid();
		$(this).attr('id',id);
	}
	var cache = wrGrid._list[id];
	if(!cache) {
		cache = new wrGrid.fn._init(grid,config);
		wrGrid._list[id] = cache;
	}
	return  cache;
}

//jquery plugin
$.fn.wrGrid = function(config){	 
    if(arguments.length==1 && typeof arguments[0]=='object') {
		config = $.extend(wrGrid.defaults,config);
	}
	var id = $(this).attr('id') 
	if(!id) {
		id =  wrGrid.uuid();
		$(this).attr('id',id);
	}
	var cache = wrGrid._list[id];
	if(!cache) {
		cache = new wrGrid.fn._init(this,config);
		wrGrid._list[id] = cache;
	}
	if( arguments.length>=1 && typeof arguments[0]=='string') {
		//var args = Array.prototype.slice.call(arguments); 
		var args = $.makeArray(arguments).slice(1);
		//args.splice(0,1);
		//fn.apply(this, args);
		cache[config].apply(null,args);
	}
	return  cache;
}

wrGrid.uuid = function() {
	return 'wrGrid'+new Date().getTime();
}

wrGrid.extend = wrGrid.fn = wrGrid.prototype = {
	constructor:wrGrid,
	elements : {
		grid_bar   : '.wr-grid-actionbar',
		grid_hd    : '.wr-grid-header',
		grid_hd_rz : '.wr-grid-header-resize',
		grid_data  : '.wr-grid-data'
	},
	
	_init : function(grid,config) {
		var _self = this,
			col,
			fileName;
		_self.parameters = config;
		//合并cols
		if(!_self.parameters.cols) _self.parameters.cols=[];
		var target_len = _self.parameters.cols.length;
		$(_self.elements.grid_hd + ' .wr-grid-header-col',_self.grid).each(function(i){
			fileName = $(this).parent().attr('fieldname');
			if(target_len >= (i+1)) {
				col = _self.parameters.cols[i];
				if(fileName && fileName!='') col.name = fileName;
			} else {
				_self.parameters.cols.push({name:fileName});
			}
		});
		//grid
		_self.grid = $(grid);
		//event
		_self._eventColWidthResize();
		_self._eventGridScroll();
		_self._eventColSort();
		_self._eventBar();
		_self._eventRefresh();
		return _self;
	},
	
	/**
	 * 列宽度改变事件
	**/
	_eventColWidthResize: function() {
		var _self = this;
		
		var hasMove = false; //是否移动中
		var startX = 0;
		var initWidth = 0;
		var col = 0;
		$(_self.elements.grid_hd_rz, _self.grid).each(function(index){
			$(this).mousedown(function(e) {
				hasMove   = true;
				startX    = e.clientX;
				var cell  = $(this).parent();
				initWidth =  $(cell).outerWidth();
				col = index;
			})
		});
		
		$(_self.grid).mousemove(function(e) {
			if(hasMove) {
				var width = initWidth + (e.clientX-startX);
				if( width > 2 ) {  //改变的宽度
					$($(_self.elements.grid_hd   +' table col',_self.grid)[col]).width(width); //表头宽度
					$($(_self.elements.grid_data +' table col',_self.grid)[col]).width(width); //数据宽度
				}
			}
		})
		
		$(_self.grid).mouseup(function(e) {
			hasMove = false;
		})
	},
	
	/**
	 * 模向滚动数据
	**/
	_eventGridScroll: function() {
		var _self = this;
		
		$(_self.elements.grid_data, _self.gird).scroll(function(e){
			$(_self.elements.grid_hd +' table',_self.grid).css('marginLeft','-'+$(this).scrollLeft()+'px');						   	
		});
	},
	
	/**
	 * 列排序事件
	**/
	_eventColSort: function() {
		var _self = this;
		
		$(_self.elements.grid_hd   +' table .wr-grid-header-col-sortable' , _self.grid).each(function(index){
			//动态效果
			$(this).mouseover(function(){
				$(this).addClass('wr-grid-header-col-sortable-over');
			}).mouseout(function(){
				$(this).removeClass('wr-grid-header-col-sortable-over');
			});
			
			//点击排序
			$(this).click(function(){
				 var fieldName = $(this).attr('fieldname'),
				     objSort = $('.wr-grid-header-sort',this),
				 	 sortType = $(objSort).hasClass('asc') ? 'desc' : 'asc';
					
				 //排除其它行标识
				 $(_self.elements.grid_hd   +' table td.wr-grid-header-col-sortable .asc',_self.grid).removeClass('asc');
				 $(_self.elements.grid_hd   +' table td.wr-grid-header-col-sortable .desc',_self.grid).removeClass('desc');
				 
				 //添加新标识
				 $(objSort).removeClass('desc').addClass(sortType);
				 $(_self.elements.grid_data +' table col.wr-grid-data-sort',_self.grid).removeClass('wr-grid-data-sort');
				 
				 //data 数据显示突出  cls:wr-grid-data-sort
				 $($(_self.elements.grid_data +' table col',_self.grid)[index]).addClass('wr-grid-data-sort');
				 //ajax data
				 _self.parameters.data[sortType] = fieldName;
				 _self.load(1);
			});		
		});
	},
	
	/**
	 * grid bar 事件
	 **/
	_eventBar:	function() {
		var _self = this;
		
		$(_self.elements.grid_bar +' li.wr-grid-actionbar-item',_self.grid).mouseover(function(){
			$(this).addClass('hover');
		}).mouseout(function(){
			$(this).removeClass('hover');
		});
	},
	
	/**
	 * grid 数据刷新
	 **/
	_eventRefresh: function() {
		var _self = this;
		$('.wr-grid-header-refresh',_self.grid).click(function(){
			_self.load(1);
		});
	},
	
	/**
	 *  清空grid 表格数据
	 **/
	clearGridData: function() {
		var _self = this;
		$(_self.elements.grid_data +' table tbody',_self.grid).empty();
		 
		return _self;
	},
	
	/**
	 *  grid表格状态栏
	 *  * @param	{String, String}  状态栏要显示的内容
	 */
	status: function (html){
		var _self = this;
		$('.wr-grid-footer .statusbar',_self.grid).html(html);
	},
	
	/**
	 *  grid 翻页
	 *  * @param	{Object, String}  页内容对象
	 *  @example 
	       Page :{
			   page: 1,
			   pageSize: 20,
			   totalPage: 20,
			   first: 1...,
			   pageNum: [1,2,3,...],
			   last: ...10  
		   }
	 */
	paging: function(page) {
		var _self = this;
		    container_paging  = $('.wr-grid-footer .paging',_self.grid);
			
		if(page && page.totalPage > 0 ) {
			container_paging.empty();
			$('<span class="pg-go">转至 <input type="text"  value="'+page.page+'" class="pg-input" maxlength="7"/>/'+page.totalPage+'页</span>')
				.appendTo(container_paging)
				.find('.pg-input')
				.keydown(function(e){
					if(e.keyCode == 13) {
						var currentPage  = parseInt($(this).val());
						if(!currentPage) {
							currentPage = 1;
							$(this).val(currentPage);
						}
						if( currentPage > page.totalPage ) {
							currentPage = page.totalPage;
							$(this).val(currentPage);
						}
						currentPage = currentPage <=0  ? 1 : currentPage;
						_self.load(currentPage);
					}
				});
			 $('<span class="page-prev prev" title="上一页"></span>').appendTo(container_paging).click(function(){
				  if(page.page-1<0) return;
				  page.page - 1 ==0 ? '' : _self.load(page.page - 1);
			 });
			 if(page.first) {
				 $('<a href="javascript:;">1...</a>').appendTo(container_paging).click(function() {
					 _self.load(1);
				 });
			 }
			 if(page.pageNum) {
				 for(var p in page.pageNum) {
					 if(page.pageNum[p] == page.page ){
						 $('<span class="activate">'+page.pageNum[p]+'</span>').appendTo(container_paging);
					 } else {
						 $('<a href="javascript:;">'+page.pageNum[p]+'</a>').appendTo(container_paging)
						 .click(function() {
							_self.load(parseInt($(this).text()));
						 });
					 }
				 }
			 }
			 if(page.last) {
				 $('<a href="javascript:;">...'+page.last+'</a>').appendTo(container_paging)
				 .click(function() {
					 _self.load(page.last);
				 });
			 }
			 $('<span class="page-next next" title="下一页"></span>').appendTo(container_paging).click(function(){
				  page.page + 1 > page.totalPage ? _self.load(page.totalPage)  : _self.load(page.page +1);
			 });
		}
	},
	
	/**
	 * 加载gird
	 * @param	{Number, String}  当前页
	 **/
	load: function(page) {
		var _self = this,
			field = _self.parameters.data;
			
		field.page  = !page || parseInt(page) == 0 ? 1 : page;
		
		_self.clearGridData()
			 ._loading(true);
			 
		$.ajax({
			dateType: _self.parameters.dataType,
			url:      _self.parameters.url,
			data:     field,
			success:  function(data){
				
				if(!data) {
					return;
				}
				if(_self.parameters.cols==null || _self.parameters.cols.length == 0) {
					return;
				}
				_self.fillGrid(data[_self.parameters.read.root])
					 ._loading(false)
					 .paging( _self._buildPage(data[_self.parameters.read.root][_self.parameters.read.page]
											   ,field.pageSize
											   ,data[_self.parameters.read.root][_self.parameters.read.records]
						)
					 );
			}
		});
	},
	
	fillGrid: function(data) {
		var _self = this,
		    elements = data[_self.parameters.read.cell];
			
		if(elements && elements.length>0) {
			var html = '',
				value = '',
				row,
				col;
			for (var i  in elements ) {
				html +='<tr>';
				row = elements[i];
				for(var j in  _self.parameters.cols ) {
					col = _self.parameters.cols[j];
					if(col.format) {
						value =  col.format(row[col.name],row) ;
					} else {
				 		value = row[col.name];
					}
					html += '<td>'+ (value ?  value : '')+'</td>';
				}
				html+='<td></td>'
				html +='</tr>';
			}
			 
			$(_self.elements.grid_data +' table tbody',_self.grid).html(html);
		}
		return _self;
	},
	
	_gridStatus: function(show,template) {
		var _self = this,
			show = (typeof show == 'string' && show  =='show') || show  ?  true : false,
			cls  = template == 'loading' ?  ' .wr-grid-loading' :  ' .wr-grid-nodata' ,
		    grid_status_wrap =  $(_self.elements.grid_data +cls,_self.grid),
			grid_data_container =  $(_self.elements.grid_data +' div:first',_self.grid);
		 
		if(grid_status_wrap.size()==0) grid_status_wrap = $(wrGrid._templates[template]).appendTo($(_self.elements.grid_data , _self.grid));
       
		if(show) {
			grid_status_wrap.show();
			grid_data_container.hide();
		} else {
			grid_status_wrap.hide();
			grid_data_container.show();
		}
		return _self;
	},
	
	_loading: function(show) {
		return this._gridStatus(show,'loading');
	},
	
	_noData: function(show) {
		return this._gridStatus(show,'nodata');
	},
	
	_buildPage: function(page,pagesize,rowcount) {

		page = page<0 ? 1 : page;
		if(page > totalPage) {
			page = totalPage;
		}
		
		var totalPage =Math.ceil(rowcount/pagesize),
			num=9;
		    result={page:page,totalPage:totalPage,pageSize:pagesize},
			//显示的最大页数
			maxPageNum = page + (num - Math.ceil(num/2));  
		
		//总页数比显示的页脚数小
		result.pageNum=[];
		
		//修正maxPageNum
		if(maxPageNum > totalPage ) {
			maxPageNum = totalPage;
			
		} else if(maxPageNum < num ) {
			maxPageNum =  num >= totalPage ?  totalPage : num;
		}
		
		//快速导航每一页
		if( maxPageNum > num ) {
			result.first = 1;
		}
		//快速导航最后一页
		if(maxPageNum < totalPage) {
			result.last = totalPage;
		}
		var start = maxPageNum < num ? 1 : maxPageNum - num +1;
		for(var i=start;i<=maxPageNum; i++) {
			result.pageNum.push(i);
		}
		return result;
	}
}

//模板
wrGrid._templates = {
	loading: '<div class="wr-grid-loading"><div class="loading-info"><div class="loading-txt">正在加载...</div></div></div>',
	nodata: '<div class="wr-grid-nodata"><div class="nodata-txt">无数据</div></div>'
};

wrGrid._list = {};
wrGrid.defaults = {type:'POST',
	dataType:'json',
	data:{pageSize:20,page:1},
	cols:[],
	read:{
	  root:'page',
	  page:'currentPage',
	  records:'recordCount',
	  cell:'elements'
	}
};

wrGrid.fn._init.prototype  = wrGrid.fn;
window.wrGrid = wrGrid;

})(jQuery,window);