!function($){var trace=function(msg){if("undefined"!=typeof window&&window.console){for(var len=arguments.length,args=[],i=0;len>i;i++)args.push("arguments["+i+"]");eval("console.log("+args.join(",")+")")}},dirname=function(e){var t=e.replace(/^\/?(.*?)\/?$/,"$1").split("/");return t.pop(),"/"+t.join("/")},basename=function(e){var t=e.replace(/^\/?(.*?)\/?$/,"$1").split("/"),n=t.pop();return""==n?null:n},_ordinalize_re=/(\d)(?=(\d\d\d)+(?!\d))/g,ordinalize=function(e){var t=""+e;return 11e3>e?t=(""+e).replace(_ordinalize_re,"$1,"):1e6>e?t=Math.floor(e/1e3)+"k":1e9>e&&(t=(""+Math.floor(e/1e3)).replace(_ordinalize_re,"$1,")+"m"),t},nano=function(e,t){return e.replace(/\{([\w\-\.]*)}/g,function(e,n){var i=n.split("."),o=t[i.shift()];return $.each(i,function(){o=o.hasOwnProperty(this)?o[this]:e}),o})},objcopy=function(e){if(void 0===e)return void 0;if(null===e)return null;if(e.parentNode)return e;switch(typeof e){case"string":return e.substring(0);case"number":return e+0;case"boolean":return e===!0}var t=$.isArray(e)?[]:{};return $.each(e,function(e,n){t[e]=objcopy(n)}),t},objmerge=function(e,t){e=e||{},t=t||{};var n=objcopy(e);for(var i in t)n[i]=t[i];return n},objcmp=function(e,t,n){if(!e||!t)return e===t;if(typeof e!=typeof t)return!1;if("object"!=typeof e)return e===t;if($.isArray(e)){if(!$.isArray(t))return!1;if(e.length!=t.length)return!1}else{var i=[];for(var o in e)e.hasOwnProperty(o)&&i.push(o);var r=[];for(var o in t)t.hasOwnProperty(o)&&r.push(o);if(n||(i.sort(),r.sort()),i.join(",")!==r.join(","))return!1}var a=!0;return $.each(e,function(n){var i=objcmp(e[n],t[n]);return a=a&&i,a?void 0:!1}),a},objkeys=function(e){var t=[];return $.each(e,function(n){e.hasOwnProperty(n)&&t.push(n)}),t},objcontains=function(e){if(!e||"object"!=typeof e)return!1;for(var t=1,n=arguments.length;n>t;t++)if(e.hasOwnProperty(arguments[t]))return!0;return!1},uniq=function(e){for(var t=e.length,n={},i=0;t>i;i++)n[e[i]]=!0;return objkeys(n)},arbor_path=function(){var e=$("script").map(function(){var e=$(this).attr("src");if(e)return e.match(/arbor[^\/\.]*.js|dev.js/)?e.match(/.*\//)||"/":void 0});return e.length>0?e[0]:null},Kernel=function(e){var t="file:"==window.location.protocol&&navigator.userAgent.toLowerCase().indexOf("chrome")>-1,n=void 0!==window.Worker&&!t,i=null,o=null,r=[];r.last=new Date;var a=null,d=null,s=null,u=null,c=!1,p={system:e,tween:null,nodes:{},init:function(){o="undefined"!=typeof Tween?Tween():"undefined"!=typeof arbor.Tween?arbor.Tween():{busy:function(){return!1},tick:function(){return!0},to:function(){trace("Please include arbor-tween.js to enable tweens"),o.to=function(){}}},p.tween=o;var t=e.parameters();return n?(trace("using web workers"),a=setInterval(p.screenUpdate,t.timeout),i=new Worker(arbor_path()+"arbor.js"),i.onmessage=p.workerMsg,i.onerror=function(e){trace("physics:",e)},i.postMessage({type:"physics",physics:objmerge(t,{timeout:Math.ceil(t.timeout)})})):(trace("couldn't use web workers, be careful..."),i=Physics(t.dt,t.stiffness,t.repulsion,t.friction,p.system._updateGeometry),p.start()),p},graphChanged:function(e){n?i.postMessage({type:"changes",changes:e}):i._update(e),p.start()},particleModified:function(e,t){n?i.postMessage({type:"modify",id:e,mods:t}):i.modifyNode(e,t),p.start()},physicsModified:function(e){isNaN(e.timeout)||(n?(clearInterval(a),a=setInterval(p.screenUpdate,e.timeout)):(clearInterval(s),s=null)),n?i.postMessage({type:"sys",param:e}):i.modifyPhysics(e),p.start()},workerMsg:function(e){var t=e.data.type;"geometry"==t?p.workerUpdate(e.data):trace("physics:",e.data)},_lastPositions:null,workerUpdate:function(e){p._lastPositions=e,p._lastBounds=e.bounds},_lastFrametime:(new Date).valueOf(),_lastBounds:null,_currentRenderer:null,screenUpdate:function(){var e=((new Date).valueOf(),!1);if(null!==p._lastPositions&&(p.system._updateGeometry(p._lastPositions),p._lastPositions=null,e=!0),o&&o.busy()&&(e=!0),p.system._updateBounds(p._lastBounds)&&(e=!0),e){var t=p.system.renderer;if(void 0!==t){t!==d&&(t.init(p.system),d=t),o&&o.tick(),t.redraw();var n=r.last;r.last=new Date,r.push(r.last-n),r.length>50&&r.shift()}}},physicsUpdate:function(){o&&o.tick(),i.tick();var e=p.system._updateBounds();o&&o.busy()&&(e=!0);var t=p.system.renderer,n=new Date,t=p.system.renderer;void 0!==t&&(t!==d&&(t.init(p.system),d=t),t.redraw({timestamp:n}));var a=r.last;r.last=n,r.push(r.last-a),r.length>50&&r.shift();var c=i.systemEnergy();(c.mean+c.max)/2<.05?(null===u&&(u=(new Date).valueOf()),(new Date).valueOf()-u>1e3&&(clearInterval(s),s=null)):u=null},fps:function(e){if(void 0!==e){var t=1e3/Math.max(1,targetFps);p.physicsModified({timeout:t})}for(var n=0,i=0,o=r.length;o>i;i++)n+=r[i];var a=n/Math.max(1,r.length);return isNaN(a)?0:Math.round(1e3/a)},start:function(e){null===s&&(!c||e)&&(c=!1,n?i.postMessage({type:"start"}):(u=null,s=setInterval(p.physicsUpdate,p.system.parameters().timeout)))},stop:function(){c=!0,n?i.postMessage({type:"stop"}):null!==s&&(clearInterval(s),s=null)}};return p.init()},Node=function(e){this._id=_nextNodeId++,this.data=e||{},this._mass=void 0!==e.mass?e.mass:1,this._fixed=e.fixed===!0?!0:!1,this._p=new Point("number"==typeof e.x?e.x:null,"number"==typeof e.y?e.y:null),delete this.data.x,delete this.data.y,delete this.data.mass,delete this.data.fixed},_nextNodeId=1,Edge=function(e,t,n){this._id=_nextEdgeId--,this.source=e,this.target=t,this.length=void 0!==n.length?n.length:1,this.data=void 0!==n?n:{},delete this.data.length},_nextEdgeId=-1,Particle=function(e,t){this.p=e,this.m=t,this.v=new Point(0,0),this.f=new Point(0,0)};Particle.prototype.applyForce=function(e){this.f=this.f.add(e.divide(this.m))};var Spring=function(e,t,n,i){this.point1=e,this.point2=t,this.length=n,this.k=i};Spring.prototype.distanceToParticle=function(e){var t=that.point2.p.subtract(that.point1.p).normalize().normal(),n=e.p.subtract(that.point1.p);return Math.abs(n.x*t.x+n.y*t.y)};var Point=function(e,t){e&&e.hasOwnProperty("y")&&(t=e.y,e=e.x),this.x=e,this.y=t};Point.random=function(e){return e=void 0!==e?e:5,new Point(2*e*(Math.random()-.5),2*e*(Math.random()-.5))},Point.prototype={exploded:function(){return isNaN(this.x)||isNaN(this.y)},add:function(e){return new Point(this.x+e.x,this.y+e.y)},subtract:function(e){return new Point(this.x-e.x,this.y-e.y)},multiply:function(e){return new Point(this.x*e,this.y*e)},divide:function(e){return new Point(this.x/e,this.y/e)},magnitude:function(){return Math.sqrt(this.x*this.x+this.y*this.y)},normal:function(){return new Point(-this.y,this.x)},normalize:function(){return this.divide(this.magnitude())}};var ParticleSystem=function(e,t,n,i,o,r,a){var d=[],s=null,u=0,c=null,p=.04,l=[20,20,20,20],f=null,y=null;if("object"==typeof t){var h=t;n=h.friction,e=h.repulsion,o=h.fps,r=h.dt,t=h.stiffness,i=h.gravity,a=h.precision}n=isNaN(n)?.5:n,e=isNaN(e)?1e3:e,o=isNaN(o)?55:o,t=isNaN(t)?600:t,r=isNaN(r)?.02:r,a=isNaN(a)?.6:a,i=i===!0;var m,v=void 0!==o?1e3/o:20,g={repulsion:e,stiffness:t,friction:n,dt:r,gravity:i,precision:a,timeout:v},_={renderer:null,tween:null,nodes:{},edges:{},adjacency:{},names:{},kernel:null},x={parameters:function(e){return void 0!==e&&(isNaN(e.precision)||(e.precision=Math.max(0,Math.min(1,e.precision))),$.each(g,function(t){void 0!==e[t]&&(g[t]=e[t])}),_.kernel.physicsModified(e)),g},fps:function(e){return void 0===e?_.kernel.fps():void x.parameters({timeout:1e3/(e||50)})},start:function(){_.kernel.start()},stop:function(){_.kernel.stop()},addNode:function(e,t){t=t||{};var n=_.names[e];if(n)return n.data=t,n;if(void 0!=e){var i=void 0!=t.x?t.x:null,o=void 0!=t.y?t.y:null,r=t.fixed?1:0,a=new Node(t);return a.name=e,_.names[e]=a,_.nodes[a._id]=a,d.push({t:"addNode",id:a._id,m:a.mass,x:i,y:o,f:r}),x._notify(),a}},pruneNode:function(e){var t=x.getNode(e);"undefined"!=typeof _.nodes[t._id]&&(delete _.nodes[t._id],delete _.names[t.name]),$.each(_.edges,function(e,n){(n.source._id===t._id||n.target._id===t._id)&&x.pruneEdge(n)}),d.push({t:"dropNode",id:t._id}),x._notify()},getNode:function(e){return void 0!==e._id?e:"string"==typeof e||"number"==typeof e?_.names[e]:void 0},eachNode:function(e){$.each(_.nodes,function(t,n){if(null!=n._p.x&&null!=n._p.y){var i=null!==c?x.toScreen(n._p):n._p;e.call(x,n,i)}})},addEdge:function(e,t,n){e=x.getNode(e)||x.addNode(e),t=x.getNode(t)||x.addNode(t),n=n||{};var i=new Edge(e,t,n),o=e._id,r=t._id;_.adjacency[o]=_.adjacency[o]||{},_.adjacency[o][r]=_.adjacency[o][r]||[];var a=_.adjacency[o][r].length>0;if(a)return void $.extend(_.adjacency[o][r].data,i.data);_.edges[i._id]=i,_.adjacency[o][r].push(i);var s=void 0!==i.length?i.length:1;return d.push({t:"addSpring",id:i._id,fm:o,to:r,l:s}),x._notify(),i},pruneEdge:function(e){d.push({t:"dropSpring",id:e._id}),delete _.edges[e._id];for(var t in _.adjacency)for(var n in _.adjacency[t])for(var i=_.adjacency[t][n],o=i.length-1;o>=0;o--)_.adjacency[t][n][o]._id===e._id&&_.adjacency[t][n].splice(o,1);x._notify()},getEdges:function(e,t){return e=x.getNode(e),t=x.getNode(t),e&&t&&"undefined"!=typeof _.adjacency[e._id]&&"undefined"!=typeof _.adjacency[e._id][t._id]?_.adjacency[e._id][t._id]:[]},getEdgesFrom:function(e){if(e=x.getNode(e),!e)return[];if("undefined"!=typeof _.adjacency[e._id]){var t=[];return $.each(_.adjacency[e._id],function(e,n){t=t.concat(n)}),t}return[]},getEdgesTo:function(e){if(e=x.getNode(e),!e)return[];var t=[];return $.each(_.edges,function(n,i){i.target==e&&t.push(i)}),t},eachEdge:function(e){$.each(_.edges,function(t,n){var i=_.nodes[n.source._id]._p,o=_.nodes[n.target._id]._p;null!=i.x&&null!=o.x&&(i=null!==c?x.toScreen(i):i,o=null!==c?x.toScreen(o):o,i&&o&&e.call(x,n,i,o))})},prune:function(e){var t={dropped:{nodes:[],edges:[]}};return void 0===e?$.each(_.nodes,function(e,n){t.dropped.nodes.push(n),x.pruneNode(n)}):x.eachNode(function(n){var i=e.call(x,n,{from:x.getEdgesFrom(n),to:x.getEdgesTo(n)});i&&(t.dropped.nodes.push(n),x.pruneNode(n))}),t},graft:function(e){var t={added:{nodes:[],edges:[]}};return e.nodes&&$.each(e.nodes,function(e,n){var i=x.getNode(e);i?i.data=n:t.added.nodes.push(x.addNode(e,n)),_.kernel.start()}),e.edges&&$.each(e.edges,function(e,n){var i=x.getNode(e);i||t.added.nodes.push(x.addNode(e,{})),$.each(n,function(n,i){var o=x.getNode(n);o||t.added.nodes.push(x.addNode(n,{}));var r=x.getEdges(e,n);r.length>0?r[0].data=i:t.added.edges.push(x.addEdge(e,n,i))})}),t},merge:function(e){var t={added:{nodes:[],edges:[]},dropped:{nodes:[],edges:[]}};$.each(_.edges,function(n,i){(void 0===e.edges[i.source.name]||void 0===e.edges[i.source.name][i.target.name])&&(x.pruneEdge(i),t.dropped.edges.push(i))});var n=x.prune(function(n){return void 0===e.nodes[n.name]?(t.dropped.nodes.push(n),!0):void 0}),i=x.graft(e);return t.added.nodes=t.added.nodes.concat(i.added.nodes),t.added.edges=t.added.edges.concat(i.added.edges),t.dropped.nodes=t.dropped.nodes.concat(n.dropped.nodes),t.dropped.edges=t.dropped.edges.concat(n.dropped.edges),t},tweenNode:function(e,t,n){var i=x.getNode(e);i&&_.tween.to(i,t,n)},tweenEdge:function(e,t,n,i){if(void 0===i)x._tweenEdge(e,t,n);else{var o=x.getEdges(e,t);$.each(o,function(e,t){x._tweenEdge(t,n,i)})}},_tweenEdge:function(e,t,n){e&&void 0!==e._id&&_.tween.to(e,t,n)},_updateGeometry:function(e){if(void 0!=e){var t=e.epoch<u;m=e.energy;var n=e.geometry;if(void 0!==n)for(var i=0,o=n.length/3;o>i;i++){var r=n[3*i];t&&void 0==_.nodes[r]||(_.nodes[r]._p.x=n[3*i+1],_.nodes[r]._p.y=n[3*i+2])}}},screen:function(e){return void 0==e?{size:c?objcopy(c):void 0,padding:l.concat(),step:p}:(void 0!==e.size&&x.screenSize(e.size.width,e.size.height),isNaN(e.step)||x.screenStep(e.step),void(void 0!==e.padding&&x.screenPadding(e.padding)))},screenSize:function(e,t){c={width:e,height:t},x._updateBounds()},screenPadding:function(e,t,n,i){trbl=$.isArray(e)?e:[e,t,n,i];var o=trbl[0],r=trbl[1],a=trbl[2];void 0===r?trbl=[o,o,o,o]:void 0==a&&(trbl=[o,r,o,r]),l=trbl},screenStep:function(e){p=e},toScreen:function(e){if(f&&c){var t=l||[0,0,0,0],n=f.bottomright.subtract(f.topleft),i=t[3]+e.subtract(f.topleft).divide(n.x).x*(c.width-(t[1]+t[3])),o=t[0]+e.subtract(f.topleft).divide(n.y).y*(c.height-(t[0]+t[2]));return arbor.Point(i,o)}},fromScreen:function(e){if(f&&c){var t=l||[0,0,0,0],n=f.bottomright.subtract(f.topleft),i=(e.x-t[3])/(c.width-(t[1]+t[3]))*n.x+f.topleft.x,o=(e.y-t[0])/(c.height-(t[0]+t[2]))*n.y+f.topleft.y;return arbor.Point(i,o)}},_updateBounds:function(e){if(null!==c){y=e?e:x.bounds();var t=new Point(y.bottomright.x,y.bottomright.y),n=new Point(y.topleft.x,y.topleft.y),i=t.subtract(n),o=n.add(i.divide(2)),r=4,a=new Point(Math.max(i.x,r),Math.max(i.y,r));if(y.topleft=o.subtract(a.divide(2)),y.bottomright=o.add(a.divide(2)),!f)return $.isEmptyObject(_.nodes)?!1:(f=y,!0);var d=p;_newBounds={bottomright:f.bottomright.add(y.bottomright.subtract(f.bottomright).multiply(d)),topleft:f.topleft.add(y.topleft.subtract(f.topleft).multiply(d))};var s=new Point(f.topleft.subtract(_newBounds.topleft).magnitude(),f.bottomright.subtract(_newBounds.bottomright).magnitude());return s.x*c.width>1||s.y*c.height>1?(f=_newBounds,!0):!1}},energy:function(){return m},bounds:function(){var e=null,t=null;return $.each(_.nodes,function(n,i){if(!e)return e=new Point(i._p),void(t=new Point(i._p));var o=i._p;null!==o.x&&null!==o.y&&(o.x>e.x&&(e.x=o.x),o.y>e.y&&(e.y=o.y),o.x<t.x&&(t.x=o.x),o.y<t.y&&(t.y=o.y))}),e&&t?{bottomright:e,topleft:t}:{topleft:new Point(-1,-1),bottomright:new Point(1,1)}},nearest:function(e){null!==c&&(e=x.fromScreen(e));var t={node:null,point:null,distance:null};return $.each(_.nodes,function(n,i){var o=i._p;if(null!==o.x&&null!==o.y){var r=o.subtract(e).magnitude();(null===t.distance||r<t.distance)&&(t={node:i,point:o,distance:r},null!==c&&(t.screenPoint=x.toScreen(o)))}}),t.node?(null!==c&&(t.distance=x.toScreen(t.node.p).subtract(x.toScreen(e)).magnitude()),t):null},_notify:function(){null===s?u++:clearTimeout(s),s=setTimeout(x._synchronize,20)},_synchronize:function(){d.length>0&&(_.kernel.graphChanged(d),d=[],s=null)}};return _.kernel=Kernel(x),_.tween=_.kernel.tween||null,Node.prototype.__defineGetter__("p",function(){var e=this,t={};return t.__defineGetter__("x",function(){return e._p.x}),t.__defineSetter__("x",function(t){_.kernel.particleModified(e._id,{x:t})}),t.__defineGetter__("y",function(){return e._p.y}),t.__defineSetter__("y",function(t){_.kernel.particleModified(e._id,{y:t})}),t.__proto__=Point.prototype,t}),Node.prototype.__defineSetter__("p",function(e){this._p.x=e.x,this._p.y=e.y,_.kernel.particleModified(this._id,{x:e.x,y:e.y})}),Node.prototype.__defineGetter__("mass",function(){return this._mass}),Node.prototype.__defineSetter__("mass",function(e){this._mass=e,_.kernel.particleModified(this._id,{m:e})}),Node.prototype.__defineSetter__("tempMass",function(e){_.kernel.particleModified(this._id,{_m:e})}),Node.prototype.__defineGetter__("fixed",function(){return this._fixed}),Node.prototype.__defineSetter__("fixed",function(e){this._fixed=e,_.kernel.particleModified(this._id,{f:e?1:0})}),x},BarnesHutTree=function(){var e=[],t=0,n=null,i=.5,o={init:function(e,r,a){i=a,t=0,n=o._newBranch(),n.origin=e,n.size=r.subtract(e)},insert:function(e){for(var t=n,i=[e];i.length;){var r=i.shift(),a=r._m||r.m,d=o._whichQuad(r,t);if(void 0===t[d])t[d]=r,t.mass+=a,t.p=t.p?t.p.add(r.p.multiply(a)):r.p.multiply(a);else if("origin"in t[d])t.mass+=a,t.p=t.p?t.p.add(r.p.multiply(a)):r.p.multiply(a),t=t[d],i.unshift(r);else{var s=t.size.divide(2),u=new Point(t.origin);"s"==d[0]&&(u.y+=s.y),"e"==d[1]&&(u.x+=s.x);var c=t[d];if(t[d]=o._newBranch(),t[d].origin=u,t[d].size=s,t.mass=a,t.p=r.p.multiply(a),t=t[d],c.p.x===r.p.x&&c.p.y===r.p.y){var p=.08*s.x,l=.08*s.y;c.p.x=Math.min(u.x+s.x,Math.max(u.x,c.p.x-p/2+Math.random()*p)),c.p.y=Math.min(u.y+s.y,Math.max(u.y,c.p.y-l/2+Math.random()*l))}i.push(c),i.unshift(r)}}},applyForces:function(e,t){for(var o=[n];o.length;)if(node=o.shift(),void 0!==node&&e!==node)if("f"in node){var r=e.p.subtract(node.p),a=Math.max(1,r.magnitude()),d=(r.magnitude()>0?r:Point.random(1)).normalize();e.applyForce(d.multiply(t*(node._m||node.m)).divide(a*a))}else{var s=e.p.subtract(node.p.divide(node.mass)).magnitude(),u=Math.sqrt(node.size.x*node.size.y);if(u/s>i)o.push(node.ne),o.push(node.nw),o.push(node.se),o.push(node.sw);else{var r=e.p.subtract(node.p.divide(node.mass)),a=Math.max(1,r.magnitude()),d=(r.magnitude()>0?r:Point.random(1)).normalize();e.applyForce(d.multiply(t*node.mass).divide(a*a))}}},_whichQuad:function(e,t){if(e.p.exploded())return null;var n=e.p.subtract(t.origin),i=t.size.divide(2);return n.y<i.y?n.x<i.x?"nw":"ne":n.x<i.x?"sw":"se"},_newBranch:function(){if(e[t]){var n=e[t];n.ne=n.nw=n.se=n.sw=void 0,n.mass=0,delete n.p}else n={origin:null,size:null,nw:void 0,ne:void 0,sw:void 0,se:void 0,mass:0},e[t]=n;return t++,n}};return o},Physics=function(e,t,n,i,o){var r=BarnesHutTree(),a={particles:{},springs:{}},d={particles:{}},s=[],u=[],c=0,p={sum:0,max:0,mean:0},l={topleft:new Point(-1,-1),bottomright:new Point(1,1)},f=1e3,y={stiffness:void 0!==t?t:1e3,repulsion:void 0!==n?n:600,friction:void 0!==i?i:.3,gravity:!1,dt:void 0!==e?e:.02,theta:.4,init:function(){return y},modifyPhysics:function(e){$.each(["stiffness","repulsion","friction","gravity","dt","precision"],function(t,n){if(void 0!==e[n]){if("precision"==n)return void(y.theta=1-e[n]);if(y[n]=e[n],"stiffness"==n){var i=e[n];$.each(a.springs,function(e,t){t.k=i})}}})},addNode:function(e){var t=e.id,n=e.m,i=l.bottomright.x-l.topleft.x,o=l.bottomright.y-l.topleft.y,r=new Point(null!=e.x?e.x:l.topleft.x+i*Math.random(),null!=e.y?e.y:l.topleft.y+o*Math.random());a.particles[t]=new Particle(r,n),a.particles[t].connections=0,a.particles[t].fixed=1===e.f,d.particles[t]=a.particles[t],s.push(a.particles[t])},dropNode:function(e){var t=e.id,n=a.particles[t],i=$.inArray(n,s);i>-1&&s.splice(i,1),delete a.particles[t],delete d.particles[t]},modifyNode:function(e,t){if(e in a.particles){var n=a.particles[e];"x"in t&&(n.p.x=t.x),"y"in t&&(n.p.y=t.y),"m"in t&&(n.m=t.m),"f"in t&&(n.fixed=1===t.f),"_m"in t&&(void 0===n._m&&(n._m=n.m),n.m=t._m)}},addSpring:function(e){var t=e.id,n=e.l,i=a.particles[e.fm],o=a.particles[e.to];void 0!==i&&void 0!==o&&(a.springs[t]=new Spring(i,o,n,y.stiffness),u.push(a.springs[t]),i.connections++,o.connections++,delete d.particles[e.fm],delete d.particles[e.to])},dropSpring:function(e){var t=e.id,n=a.springs[t];n.point1.connections--,n.point2.connections--;var i=$.inArray(n,u);i>-1&&u.splice(i,1),delete a.springs[t]},_update:function(e){return c++,$.each(e,function(e,t){t.t in y&&y[t.t](t)}),c},tick:function(){y.tendParticles(),y.eulerIntegrator(y.dt),y.tock()},tock:function(){var e=[];$.each(a.particles,function(t,n){e.push(t),e.push(n.p.x),e.push(n.p.y)}),o&&o({geometry:e,epoch:c,energy:p,bounds:l})},tendParticles:function(){$.each(a.particles,function(e,t){void 0!==t._m&&(Math.abs(t.m-t._m)<1?(t.m=t._m,delete t._m):t.m*=.98),t.v.x=t.v.y=0})},eulerIntegrator:function(e){y.repulsion>0&&(y.theta>0?y.applyBarnesHutRepulsion():y.applyBruteForceRepulsion()),y.stiffness>0&&y.applySprings(),y.applyCenterDrift(),y.gravity&&y.applyCenterGravity(),y.updateVelocity(e),y.updatePosition(e)},applyBruteForceRepulsion:function(){$.each(a.particles,function(e,t){$.each(a.particles,function(e,n){if(t!==n){var i=t.p.subtract(n.p),o=Math.max(1,i.magnitude()),r=(i.magnitude()>0?i:Point.random(1)).normalize();t.applyForce(r.multiply(y.repulsion*(n._m||n.m)*.5).divide(o*o*.5)),n.applyForce(r.multiply(y.repulsion*(t._m||t.m)*.5).divide(o*o*-.5))}})})},applyBarnesHutRepulsion:function(){if(l.topleft&&l.bottomright){var e=new Point(l.bottomright),t=new Point(l.topleft);r.init(t,e,y.theta),$.each(a.particles,function(e,t){r.insert(t)}),$.each(a.particles,function(e,t){r.applyForces(t,y.repulsion)})}},applySprings:function(){$.each(a.springs,function(e,t){var n=t.point2.p.subtract(t.point1.p),i=t.length-n.magnitude(),o=(n.magnitude()>0?n:Point.random(1)).normalize();t.point1.applyForce(o.multiply(t.k*i*-.5)),t.point2.applyForce(o.multiply(t.k*i*.5))})},applyCenterDrift:function(){var e=0,t=new Point(0,0);if($.each(a.particles,function(n,i){t.add(i.p),e++}),0!=e){var n=t.divide(-e);$.each(a.particles,function(e,t){t.applyForce(n)})}},applyCenterGravity:function(){$.each(a.particles,function(e,t){var n=t.p.multiply(-1);t.applyForce(n.multiply(y.repulsion/100))})},updateVelocity:function(e){$.each(a.particles,function(t,n){if(n.fixed)return n.v=new Point(0,0),void(n.f=new Point(0,0));n.v.magnitude();n.v=n.v.add(n.f.multiply(e)).multiply(1-y.friction),n.f.x=n.f.y=0;var i=n.v.magnitude();i>f&&(n.v=n.v.divide(i*i))})},updatePosition:function(e){var t=0,n=0,i=0,o=null,r=null;$.each(a.particles,function(a,d){d.p=d.p.add(d.v.multiply(e));var s=d.v.magnitude(),u=s*s;if(t+=u,n=Math.max(u,n),i++,!o)return o=new Point(d.p.x,d.p.y),void(r=new Point(d.p.x,d.p.y));var c=d.p;null!==c.x&&null!==c.y&&(c.x>o.x&&(o.x=c.x),c.y>o.y&&(o.y=c.y),c.x<r.x&&(r.x=c.x),c.y<r.y&&(r.y=c.y))}),p={sum:t,max:n,mean:t/i,n:i},l={topleft:r||new Point(-1,-1),bottomright:o||new Point(1,1)}},systemEnergy:function(){return p}};return y.init()},_nearParticle=function(e,t){var t=t||0,n=e.x,i=e.y,o=2*t;return new Point(n-t+Math.random()*o,i-t+Math.random()*o)};return"undefined"==typeof window?function(){$={each:function(e,t){if($.isArray(e))for(var n=0,i=e.length;i>n;n++)t(n,e[n]);else for(var o in e)t(o,e[o])},map:function(e,t){var n=[];return $.each(e,function(e,i){var o=t(i);void 0!==o&&n.push(o)}),n},extend:function(e,t){if("object"!=typeof t)return e;for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n]);return e},isArray:function(e){return e?-1!=e.constructor.toString().indexOf("Array"):!1},inArray:function(e,t){for(var n=0,i=t.length;i>n;n++)if(t[n]===e)return n;return-1},isEmptyObject:function(e){if("object"!=typeof e)return!1;var t=!0;return $.each(e,function(){t=!1}),t}};var e=function(){var e=20,t=null,n=null,i=null,o=((new Date).valueOf(),{init:function(e){return o.timeout(e.timeout),t=Physics(e.dt,e.stiffness,e.repulsion,e.friction,o.tock),o},timeout:function(t){t!=e&&(e=t,null!==n&&(o.stop(),o.go()))},go:function(){null===n&&(i=null,n=setInterval(o.tick,e))},stop:function(){null!==n&&(clearInterval(n),n=null)},tick:function(){t.tick();var e=t.systemEnergy();(e.mean+e.max)/2<.05?(null===i&&(i=(new Date).valueOf()),(new Date).valueOf()-i>1e3&&o.stop()):i=null},tock:function(e){e.type="geometry",postMessage(e)},modifyNode:function(e,n){t.modifyNode(e,n),o.go()},modifyPhysics:function(e){t.modifyPhysics(e)},update:function(e){t._update(e)}});return o},t=e();onmessage=function(e){if(!e.data.type)return void postMessage("¿kérnèl?");if("physics"==e.data.type){var n=e.data.physics;return void t.init(e.data.physics)}switch(e.data.type){case"modify":t.modifyNode(e.data.id,e.data.mods);break;case"changes":t.update(e.data.changes),t.go();break;case"start":t.go();break;case"stop":t.stop();break;case"sys":var n=e.data.param||{};isNaN(n.timeout)||t.timeout(n.timeout),t.modifyPhysics(n),t.go()}}}():(arbor="undefined"!=typeof arbor?arbor:{},void $.extend(arbor,{ParticleSystem:ParticleSystem,Point:function(e,t){return new Point(e,t)},etc:{trace:trace,dirname:dirname,basename:basename,ordinalize:ordinalize,objcopy:objcopy,objcmp:objcmp,objkeys:objkeys,objmerge:objmerge,uniq:uniq,arbor_path:arbor_path}}))}(this.jQuery);