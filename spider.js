var spiderLegSockets =
  [[0,-2], [3,-1],
   [0.5,-1.5], [1,-1.5],
   [0.5,1.5], [1,1.5],
   [0,2], [3,1]];

function hiccup(tag, attributes) {
  var e = document.createElementNS("http://www.w3.org/2000/svg", tag);
  for (k in attributes) {
    e.setAttribute(k, attributes[k]);
  }
  return e;
}

function spiderLeg(socket) {
  var tr = math.multiply(socket, 3);
  if (socket[1]>0) {
    tr = math.add(tr, [-5,0]);
  }
  var l = hiccup("path", {d: "M"+socket+" L"+tr});
  l.socket = socket;
  l.down = true;
  l.tr = tr;
  return l;
}

function spider() {
  var s = hiccup("g", {stroke: "black"});
  var bodyFront = hiccup("ellipse", {cx: 2, rx: 2, ry: 1});
  s.appendChild(bodyFront);
  var bodyBack = hiccup("ellipse", {cx: -2, rx: 3, ry: 2});
  s.appendChild(bodyBack);
  s.tr = [0,0];
//  s.goal = [0];
  s.rot = [0];
  s.v = [0,1];
  for (socket of spiderLegSockets) {
    s.appendChild(spiderLeg(socket));
  }
  return s;
}

function updateSpider(s, dt) {
//  if (Math.random() < 0.001) {
//    s.rot = [Math.random()*360];
//  }
  s.tr = math.add(s.tr, [dt, 0]);
  s.setAttribute("transform", "translate("+s.tr+") rotate("+s.rot+")");
  for (leg of s.children) {
    if (leg.tr) {
      if (leg.down) {
        leg.tr = math.add(leg.tr, [-dt, 0]);
      } else {
        leg.tr = math.add(leg.tr, [4*dt, 0]);
      }
      leg.setAttribute("d", "M"+leg.socket+" L"+leg.tr);
      if (leg.tr[0] > (leg.socket[0] > 0.5 ? 9 : -5)) {
        leg.down = true;
      } else if (leg.tr[0] < (leg.socket[0] > 0.5 ? 5 : -9)) {
        leg.down = false;
      }
    }
  }
}

var world = document.getElementById("world");

var lastTs = performance.now();

function animate(ts) {
  var dt = (ts - lastTs)/100;
  lastTs = ts;
  for (child of world.children) {
    updateSpider(child, dt);
  }
  requestAnimationFrame(animate);
}

function init() {
  world.appendChild(spider());
  requestAnimationFrame(animate);
}

init();
