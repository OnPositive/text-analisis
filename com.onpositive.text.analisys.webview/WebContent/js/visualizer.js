$(function() {

	var Renderer = function(canvas) {

		var $canvas = $(canvas),
				 canvas = $canvas.get(0),
						ctx = canvas.getContext("2d"),
						gfx = arbor.Graphics(canvas),
						sys = null,
						vgn = null,

			 selected = null,
		highlighted = null

	   ctx.bp = function() { this.beginPath(); return this; }
	   ctx.cp = function() { this.closePath(); return this; }
		 ctx.m = function(x, y) { this.moveTo(x,y); return this; }
		 ctx.l = function(x, y) { this.lineTo(x,y); return this; }

		var endvec = function(pt1, pt2, radius) {
			var x = pt2.x - pt1.x,
					y = pt2.y - pt1.y,
					l = Math.sqrt(x * x + y * y),
					r = 1 - radius / (2 * l)

			return { x: x * r, y: y * r }
		}

		var draw = {
			node: function(node, pt) {
				var clr = { "SymbolToken": "#b2b19d", "StringToken": "#b2b19d", "WordFormToken": "#922e00", "SyntaxToken": "#2e0092", "LongNameToken": "#2e0092", "ClauseToken": "#00922e" },
					opt = { color: "white", align: "center", size: 12 },
				    text = {
						"SymbolToken": node.data.value,
						"StringToken": node.data.value,
						"WordFormToken": "WFT",
						"SyntaxToken": "ST",
						"LongNameToken": "LNT",
						"ClauseToken": "Clause"
					},
				w = node.data.width = Math.max(12, 12 + gfx.textWidth(text[node.data.type]))

				var alpha = (!highlighted || node == highlighted || sys.getEdges(node, highlighted).length + sys.getEdges(highlighted, node).length != 0) ? 1 : .1				

				gfx.oval(pt.x - w/2, pt.y - w/2, w, w, { fill: clr[node.data.type], alpha: alpha })				
				gfx.text(text[node.data.type], pt.x, pt.y + 7, opt)

				node.pt = pt
			},

			edge: function(edge, pt1, pt2) {
				var arrowhead = function(x, y, beta, symbol) {
					var l = 7, w = 3

					ctx.save()
					ctx.translate(x, y)
					ctx.rotate(beta)
					ctx.clearRect(-l/2, -0.5, l/2, 0.5)
					ctx.bp().m(-l, w).l(0, 0).l(-l, -w).l(-l * 0.8, -0).cp().fill()
					if (symbol == 'to')
					 	ctx.bp().m(-2 * l, w).l(-2 * l, -w).cp().stroke()					
					ctx.restore()
				}

				alpha = 0.5
				if (highlighted) alpha = (edge.target == highlighted || edge.source == highlighted ) ? 1 : 0.1

				var clr = { next: "#565656", previous: "#565656", child: "#a6a6fa", parent: "#a6a6fa" },
						opt = { stroke: clr[edge.data.type], width: 1, alpha: alpha }

				var bedge = sys.getEdges(edge.target, edge.source)
				bedge = bedge ? bedge[0] : null

				edge.drawn = true

				if (!bedge || !bedge.drawn)
					gfx.line(pt1, pt2, opt)

				ctx.fillStyle = opt.stroke

				ctx.strokeStyle = opt.stroke

				ctx.globalAlpha = alpha

				if (edge.data.type == "child" || edge.data.type == "next") {
					var vec = endvec(pt1, pt2, edge.target.data.width)
					//arrowhead(pt2.x, pt2.y, Math.atan2(vec.y, vec.x))
					arrowhead(pt1.x + vec.x, pt1.y + vec.y, Math.atan2(vec.y, vec.x), 'to')
				} else {
					var vec = endvec(pt2, pt1, edge.source.data.width)
					arrowhead(pt2.x + vec.x, pt2.y + vec.y, Math.atan2(vec.y, vec.x), 'from')
				}				
				ctx.globalAlpha = 1
			},

			vignette: function() {
//				var w = canvas.width
//				var h = canvas.height
//				var r = 20
//
//				if (!vgn){
//					var top = ctx.createLinearGradient(0,0,0,r)
//					top.addColorStop(0, "#e0e0e0")
//					top.addColorStop(.7, "rgba(255,255,255,0)")
//
//					var bot = ctx.createLinearGradient(0,h-r,0,h)
//					bot.addColorStop(0, "rgba(255,255,255,0)")
//					bot.addColorStop(1, "white")
//
//					vgn = {top:top, bot:bot}
//				}
//
//				// top
//				ctx.fillStyle = vgn.top
//				ctx.fillRect(0,0, w,r)
//
//				// bot
//				ctx.fillStyle = vgn.bot
//				ctx.fillRect(0,h-r, w,r)
			},

			tooltip: function(node) {
				var typedict = {
					00001 : "undefined",
					10001 : "digit",		
					10002 : "letter",		
					10003 : "symbol",		
					10004 : "vulgar fraction",
					10005 : "exponent",		
					100101 : "linebreak",		
					100102 : "non breaking space",	
					100103 : "other whitespace",
					10201 : "scalar",
					10202 : "date",					
					10203 : "dimension",			
					10213 : "unit",					
					10301 : "region bound",			
					11000 : "word form",
					11001 : "word with index",		
					11002 : "sentence",					
					11011 : "noun adjective",		
					11012 : "adjective adverb",	
					11013 : "verb adverb",
					11014 : "direct object name",	
					11015 : "direct object inf",	
					11016 : "verb noun",				
					11017 : "verb adjective",		
					11018 : "verb noun prep",
					11019 : "verb adjective prep",		
					11020 : "verb adverb prep",		
					11021 : "verb gerund",			
					11022 : "composite verb",		
					11023 : "noun name prep",
					11024 : "noun participle",		
					11025 : "verb particle",		
					11030 : "clause",				
					11031 : "complex clause",		
					11041 : "adverb with modificator",
					11051 : "uniform predicative",	
					11052 : "uniform adverb",		
					11053 : "uniform adjective",	
					11054 : "uniform noun",			
					11055 : "uniform verb",
					11056 : "measured noun",			
					11057 : "genitive chain",		
					11058 : "preposition group",		
					12001 : "brackets",				
					12002 : "enumeration",
					12003 : "direct speach",			
					12004 : "title",				
					20001 : "link",						
					20002 : "long name"
				}
				var d = node.data,
					value = d.value
				
				if (d.grammems) value += " [" + d.grammems.join(", ") + "]";
				
				var texts = [value, d.id,	typedict[d.subtype], d.type, d.parser, d.level],
 					labels = ["Value", "Id:", "Type:", "Class:", "Parser:", "Level:"],
 				  	width = 0,
 				  	pt = node.pt

				for (i = 0; i < texts.length; i++) if (gfx.textWidth(texts[i]) > width) width = gfx.textWidth(texts[i])

				width += 100 //
				height = 10 + texts.length * 16

				var x = pt.x + width + 20 > gfx.size().width ? pt.x - width - 50 : pt.x + 50
						y = pt.y + height+ 20 > gfx.size().height ? pt.y - height - 50 : pt.y + 50

				gfx.rect(x, y, width, height, 4, { stroke: "#dadada", fill: "white" })
				gfx.line(x, y + 20, x + width, y + 20, { stroke: "#dadada" })

				gfx.text(value, x + 40, y + 18, { fill: "white", stroke: "#dadada" })
				gfx.text(value, x + 40, y + 18, { fill: "white", stroke: "#dadada" })

				for (i = 1; i < labels.length; i++) {
					gfx.text(labels[i], x + 80 - gfx.textWidth(labels[i]), y + 24 + i * 16, { fill: "white"})
					gfx.text(labels[i], x + 80 - gfx.textWidth(labels[i]), y + 24 + i * 16, { fill: "white"})
					gfx.text(texts[i], x + 85, y + 24 + i * 16)
				}

			}
		}

		var that = {
			init: function(system) {
				sys = system

				sys.screenSize(canvas.width, canvas.height)
				sys.screenPadding(80)

				that.initMouseHandling()
			},

			redraw: function() {
				gfx.clear()

				sys.eachEdge(function(edge) { delete edge.drawn; });
				sys.eachEdge(draw.edge)
				sys.eachNode(draw.node)

				if (highlighted) draw.tooltip(highlighted)

				draw.vignette()
			},

			initMouseHandling: function() {
				var dragged = null,
						unhighlight = function() {
							if (!highlighted) return
							highlighted.data.highlighted = false
							highlighted = null
						},
						highlight = function(node) {
							highlighted = node
							highlighted.data.highlighted = true
						},
						select = function(node) {
							if (selected) unselect()
							selected = node
						},
						unselect = function() {

						}

				var handler = {
					moved: function(e) {
						var pos = $canvas.offset(),
								mouse = arbor.Point(e.pageX-pos.left, e.pageY-pos.top),
								nearest = sys.nearest(mouse)

						unhighlight()

						if (nearest && nearest.node && nearest.distance < 50)
							highlight(nearest.node)

						sys.renderer.redraw()

						return false
					},

					clicked: function(e) {
						var pos = $canvas.offset(),
								mouse = arbor.Point(e.pageX-pos.left, e.pageY-pos.top),
								nearest = sys.nearest(mouse)

						if (!nearest.node || nearest.distance > 30) return false

						dragged = nearest

						nearest.node.fixed = true

						$canvas.unbind('mousemove', handler.moved)
									 .bind('mousemove', handler.dragged)
						$(window).bind('mouseup', handler.dropped)

						return false
					},

					dragged: function(e) {
						var pos = $canvas.offset(),
							mouse = arbor.Point(e.pageX-pos.left, e.pageY-pos.top),
								 ps = sys.fromScreen(mouse)

							dragged.node.p = ps
							return false
					},

					dropped: function(e) {
						if (dragged == null || dragged.node == null) return false
						dragged.node.fixed = false
						dragged.node.tempMass = 1000
						dragged = null
						$canvas.unbind('mousemove', handler.dragged)
									 .bind(  'mousemove', handler.moved)
						$(window).unbind('mouseup', handler.dropped)

						return false
					}
				} // handler

				$canvas.bind('mousedown', handler.clicked)
							 .bind('mousemove', handler.moved)
			} // initMouseHandler
		} // that

		return that
	}


	var sys = arbor.ParticleSystem(0, 0, 0)
	sys.parameters({ gravity: false })
	sys.renderer = Renderer("#viewport")

	window.sys = window.system = sys
});