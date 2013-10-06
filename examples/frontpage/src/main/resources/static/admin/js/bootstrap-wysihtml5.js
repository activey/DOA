!function($, wysi) {
    "use strict";

    var templates = function(key, locale) {

        var tpl = {
            "font-styles": "<li class='dropdown'>" +
                               "<a class='btn dropdown-toggle' data-toggle='dropdown' href='#'>" +
                                   "<i class='icon-font'></i>&nbsp;<span class='current-font'>" + locale.font_styles.normal + "</span>&nbsp;<b class='caret'></b>" +
                               "</a>" +
                               "<ul class='dropdown-menu'>" +
                                   "<li><a data-wysihtml5-command='formatBlock' data-wysihtml5-command-value='div'>" + locale.font_styles.normal + "</a></li>" +
                                   "<li><a data-wysihtml5-command='formatBlock' data-wysihtml5-command-value='h1'>" + locale.font_styles.h1 + "</a></li>" +
                                   "<li><a data-wysihtml5-command='formatBlock' data-wysihtml5-command-value='h2'>" + locale.font_styles.h2 + "</a></li>" +
                                   "<li><a data-wysihtml5-command='formatBlock' data-wysihtml5-command-value='h3'>" + locale.font_styles.h3 + "</a></li>" +
                               "</ul>" +
                           "</li>",
            "emphasis":    "<li>" +
                               "<div class='btn-group'>" +
                                   "<a class='btn' data-wysihtml5-command='bold' title='CTRL+B'><i class='icon-bold'></i></a>" +
                                   "<a class='btn' data-wysihtml5-command='italic' title='CTRL+I'><i class='icon-italic'></i></a>" +
                                   "<a class='btn' data-wysihtml5-command='underline' title='CTRL+U'><i class='icon-underline'></i></a>" +
                               "</div>" +
                           "</li>",
            "justify":    "<li>" +
                           "<div class='btn-group'>" +
                               "<a class='btn' data-wysihtml5-command='justifyLeft'><i class='icon-align-left'></i></a>" +
                               "<a class='btn' data-wysihtml5-command='justifyCenter'><i class='icon-align-center'></i></a>" +
                               "<a class='btn' data-wysihtml5-command='justifyRight'><i class='icon-align-right'></i></a>" +
                           "</div>" +
                           "</li>",
            "lists":       "<li>" +
                               "<div class='btn-group'>" +
                                   "<a class='btn' data-wysihtml5-command='insertUnorderedList' title='" + locale.lists.unordered + "'><i class='icon-list'></i></a>" +
                                   "<a class='btn' data-wysihtml5-command='insertOrderedList' title='" + locale.lists.ordered + "'><i class='icon-th-list'></i></a>" +
                                   "<a class='btn' data-wysihtml5-command='Outdent' title='" + locale.lists.outdent + "'><i class='icon-indent-right'></i></a>" +
                                   "<a class='btn' data-wysihtml5-command='Indent' title='" + locale.lists.indent + "'><i class='icon-indent-left'></i></a>" +
                               "</div>" +
                           "</li>"
        };
        return tpl[key];
    };

    var defaultOptions = {
        "font-styles": true,
        "justify": true,
        "emphasis": true,
        "lists": true,
        events: {},
        parserRules: {
            tags: {
                "b":  {},
                "i":  {},
                "br": {},
                "ol": {},
                "ul": {},
                "li": {},
                "h1": {},
                "h2": {},
                "h3": {},
                "blockquote": {},
                "u": 1,                
                "a":  {
                    set_attributes: {
                        target: "_blank",
                        rel:    "nofollow"
                    },
                    check_attributes: {
                        href:   "url" // important to avoid XSS
                    }
                },
                "span": 1,
                "div": 1
            }
        },
        stylesheets: ["../../../common/css/wysihtml5-formatting.css"],
        locale: "en"
    };

    var Wysihtml5 = function(el, options) {
        this.el = el;
        this.toolbar = this.createToolbar(el, options || defaultOptions);
        this.editor =  this.createEditor(options);

        window.editor = this.editor;

        $('iframe.wysihtml5-sandbox').each(function(i, el){
            $(el.contentWindow).off('focus.wysihtml5').on({
              'focus.wysihtml5' : function(){
                 $('li.dropdown').removeClass('open');
               }
            });
        });
    };

    Wysihtml5.prototype = {

        constructor: Wysihtml5,

        createEditor: function(options) {
          options = $.extend({}, defaultOptions, options || {});
		    options.toolbar = this.toolbar[0];

		    var editor = new wysi.Editor(this.el[0], options);

            if(options && options.events) {
                for(var eventName in options.events) {
                    editor.on(eventName, options.events[eventName]);
                }
            }

            return editor;
        },

        createToolbar: function(el, options) {
            var self = this;
            var toolbar = $("<ul/>", {
                'class' : "wysihtml5-toolbar",
                'style': "display:none"
            });
	          var culture = options.locale || defaultOptions.locale || "en";
            for(var key in defaultOptions) {
                var value = false;

                if(options[key] !== undefined) {
                    if(options[key] === true) {
                        value = true;
                    }
                } else {
                    value = defaultOptions[key];
                }

                if(value === true) {
                    toolbar.append(templates(key, locale[culture]));
                }
            }

            if(options.toolbar) {
                for(key in options.toolbar) {
                   toolbar.append(options.toolbar[key]);
                }
            }

            toolbar.find("a[data-wysihtml5-command='formatBlock']").click(function(e) {
                var target = e.target || e.srcElement;
                var el = $(target);
                self.toolbar.find('.current-font').text(el.html());
            });

            toolbar.find("a[data-wysihtml5-command='foreColor']").click(function(e) {
                var target = e.target || e.srcElement;
                var el = $(target);
                self.toolbar.find('.current-color').text(el.html());
            });

            this.el.before(toolbar);

            return toolbar;
        },
        
    };

    $.fn.wysihtml5 = function (options) {
        return this.each(function () {
            var $this = $(this);
            $this.data('wysihtml5', new Wysihtml5($this, options));
        });
    };

    $.fn.wysihtml5.Constructor = Wysihtml5;

    var locale = $.fn.wysihtml5.locale = {
        en: {
            font_styles: {
                normal: "Zwykły tekst",
                h1: "Nagłówek 1",
                h2: "Nagłówek 2",
                h3: "Nagłówek 3"
            },
            emphasis: {
                bold: "Pogrubiony",
                italic: "Pochylony",
                underline: "Podkreślony"
            },
            lists: {
                unordered: "Lista wypunktowana",
                ordered: "Lista numerowana",
                indent: "Zwiększ wcięcie",
                outdent: "Zmniejsz wcięcie"
            }
        }
    };

}(window.jQuery, window.wysihtml5);
