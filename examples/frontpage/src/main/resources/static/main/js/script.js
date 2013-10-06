$(document).ready(function() {
	// hover
	$('.bg > div').not('.active').css({opacity:0})
	
	$('.link1, .link2').each(function(){
		color=$(this).css('color');
		$(this).prepend('<span></span>');
		$(this).find('span').css({background:color})
	})
	
	$('.link1').hover(function(){
		$(this).find('span').css({width:0, opacity:1}).stop().animate({width:'100%'})
	}, function(){
		$(this).find('span').stop().animate({opacity:0})
	})
	
	$('.link2').hover(function(){
		$(this).find('span').stop().animate({opacity:0})					   
	}, function(){
		$(this).find('span').css({width:0, opacity:1}).stop().animate({width:'100%'})
	})
	
	$('#icons .img_act').css({opacity:0})
	
	$('#icons a').hover(function(){
		$(this).find('.img_act').stop().animate({opacity:0.6})						 
	}, function(){
		$(this).find('.img_act').stop().animate({opacity:0})						 
	})
	
	$('.close span').css({opacity:0})
	
	$('.close').hover(function(){
		$(this).find('span').stop().animate({opacity:1})
	}, function(){
		$(this).find('span').stop().animate({opacity:0})
	})
	
	$('.list1 a').hover(function(){
		$(this).stop().animate({color:'#2d2d2d', paddingLeft:32},800,'easeOutElastic')					 
	}, function(){
		$(this).stop().animate({color:'#797979', paddingLeft:23},800,'easeOutElastic')					 
	})
	
	$('.submenu_1 span').css({opacity:0.2})
	
	$('.submenu_1 li').hover(function(){
		$(this).find('> span').stop().animate({opacity:1})
	} ,function(){
		if (!$(this).hasClass('sfHover')) {
			$(this).find('> span').stop().animate({opacity:0.2})
		}
	})
	
	$('ul#menu').superfish({
      delay:       600,
      animation:   {height:'show'},
      speed:       600,
      autoArrows:  false,
      dropShadows: false
    });
	
	$('.back span').css({opacity:0})
	
	$('.back').hover(function(){
		$(this).stop().animate({color:'#919191'})					  
		$(this).find('span').stop().animate({opacity:1})					  
	}, function(){
		$(this).stop().animate({color:'#2d2d2d'})					  
		$(this).find('span').stop().animate({opacity:0})					  
	})
	
 });


var w_cont=970, w_line=50, w=$(window).width();

function initSection(section) {
	var _section = section[0];
	$(_section).find('.scroll').mCustomScrollbar();

	$(_section).find('.gallery ul').stop().css({left:10});
	$(_section).find('.gallery').mousemove(function(e){
		var ul=$(this).find('ul')
		var max_pos=w-ul.width();
		if (e.pageX<w_line) {
			ul.stop().animate({left:10},2000)
		} else {
			if (e.pageX>w-w_line) {
				ul.stop().animate({left:max_pos},2000)
			} else {
				ul.stop();
			}
		}
	})
	$(_section).find('.gallery').mousewheel(function(e, d){
		var ul=$(this).find('ul')
		var max_pos=w-ul.width();
		var left_pos=parseInt(ul.css('left'))
		if (d<0) {
			if (left_pos-204>max_pos) {
				left_pos=left_pos-204;
			} else {left_pos=max_pos}
		}
		if (d>0) {
			if	(left_pos+204<10) {
				left_pos=left_pos+204;
			} else {
				left_pos=10;
			}
		}
		ul.stop().animate({left:left_pos})
		return false
	});

	$(_section).find('.gallery').each(function(){
		var amount=$(this).find('> ul > li').length;
		$(this).data({amount:amount})
		$(this).find('> ul').css({width:amount*204})
	})

	$(_section).find('.gallery img').css({opacity:0.5})
	$(_section).find('.gallery span').css({opacity:0})
	/*$('.gallery a').hover(
		function(){
			$(this).removeClass("g")
		},
		function(){
			$(this).addClass("g");
		}
	)*/
	$(_section).find('.gallery a').hover(function(){
		$(this).find('img').stop().animate({opacity:1})
		$(this).find('span').stop().animate({opacity:0})
	}, function(){
		$(this).find('img').stop().animate({opacity:0.5})
		$(this).find('span').stop().animate({opacity:0})
	})

	// fancybox
	$(_section).find(".gallery a").fancybox({
		'transitionIn'		: 'none',
		'transitionOut'		: 'none',
		'title'             : this.title
	});

}

$(window).load(function() {
    w_cont=970, w_line=50, w=$(window).width();
	// bg
	$('#menu > li').hover(function(){
		img=$(this).data('type');
		$('.bg > div').stop().animate({opacity:0},800)
		$(img).stop().animate({opacity:1},800)
	}, function(){})
	
	
	//content switch
	var content=$('#content'),
		nav=$('.menu');
	nav.navs({
		useHash:true,
		hoverIn:function(li){
			$('> span',li).stop().animate({opacity:1})			
		},
		hoverOut:function(li){
			if (!li.hasClass('with_ul') || !li.hasClass('sfHover')) {
				$('> span',li).stop().animate({opacity:0.2})
			}
		}				
	})	
	content.tabs({
		actFu:function(_){
			if (_.prev && _.curr) {
				_.prev.find('.gallery').stop().animate({left:-2800}, 600, 'easeInBack')
					_.prev.find('.back').stop().animate({left:-1800}, 200)
					_.prev.find('.box').stop().animate({left:-1800}, 600, 'easeInBack')
					_.prev.find('h2').stop().animate({left:-1800}, 600, function(){
						_.prev.css({display:'none'})				
						_.curr.css({display:'block'})
						_.curr.find('h2').stop().animate({left:0}, 600, 'easeOutBack')
						_.curr.find('.box').stop().delay(200).animate({left:0}, 600, 'easeOutBack')
						_.curr.find('.gallery').stop().delay(200).animate({left:0}, 600, 'easeOutBack')
						_.curr.find('.back').stop().animate({left:0}, 200)
					})
			} else {
				if (_.curr) {
					for(var i=0;i<$('#menu > li').length;i++) {
						$('#menu > li').eq(i).stop().delay(i*200).animate({left:-1800}, 600, 'easeInBack')
					}
					$.when($('#menu > li')).then(function(){
						// loading content div using Wicket call
                        var wicketCallbackUrl = _.curr.attr('data-wicket-url');
                        if (wicketCallbackUrl) {
                        	$('.menu').css({display:'none'})
							$('#content > ul').css({display:'block'})
							_.curr.css({display:'block'})

                        	var loaded = _.curr.data('section-loaded');
							if (!loaded) {
							// loading content from wicket
								Wicket.Ajax.get({'u': wicketCallbackUrl, 'async': false});
								$(_.curr).waitForImages(function() {
									_.curr.removeClass('section_spinner');
									$('div', _.curr).fadeIn();
									_.curr.data('section-loaded', true);
									initSection(_.curr);
								});
							}

							_.curr.find('h2').stop().animate({left:0}, 600, 'easeOutBack')
							_.curr.find('.box').stop().delay(200).animate({left:0}, 600, 'easeOutBack')
							_.curr.find('.gallery').stop().delay(200).animate({left:0}, 600, 'easeOutBack')
							_.curr.find('.back').stop().animate({left:0}, 200)
                        }


					})
				}
				if (_.prev) {
					_.prev.find('.gallery').stop().animate({left:-2800}, 600, 'easeInBack')
					_.prev.find('.back').stop().animate({left:-1800}, 200)
					_.prev.find('.box').stop().animate({left:-1800}, 600, 'easeInBack')
					_.prev.find('h2').stop().animate({left:-1800}, 600, function(){
						_.prev.css({display:'none'})				
						$('#content > ul').css({display:'none'})					
						$('.menu').css({display:'block'})		
						for(var i=0;i<$('#menu > li').length;i++) {
							$('#menu > li').eq(i).stop().delay(i*200).animate({left:0}, 600, 'easeOutBack')
						}
					})
				}
			}
		},
		preFu:function(_){						
			$('#content > ul').css({display:'none'})
			$('#content > ul > li').css({ position:'absolute', display:'none'})
			$('#content h2').css({left:-1800, opacity:0.2})
			$('#content .box').css({left:-1800})
			$('#content .gallery').css({left:-2800})
			$('#content .back').css({left:-1800})
		}
	})
	nav.navs(function(n, _){		
		if (n=='close' || n=='#!/') {
			content.tabs(n);
		} else {
			content.tabs(n);
		}
	})
	
	
	/*
	var m_top=0
	function centre() {
		
	}
	centre();
	$(window).resize(centre);
	*/
	var h_cont=954;
	function centre() {
		w=$(window).width();
		var h=$(window).height();
		if (w>w_cont) {
			w_line=(w-w_cont)/2+50;
		} else {
			w_line=50;
			w=w_cont;
		}
		$('.top_line').css({width:w_line})
		//gallery();
		if (h>h_cont) {
			m_top=(h-h_cont)/2+32;
		} else {
			m_top=32
		}
		$('.center').stop().animate({marginTop:m_top})
	}
	centre();
	$(window).resize(centre);
})