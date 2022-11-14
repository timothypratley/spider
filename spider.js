var spiderLegSockets =
  [[-3,-1], [3,-1],
   [-2,-2], [2,-2],
   [-2,2], [2,2],
   [-3,1], [3,1]];

function spiderLeg(socket) {
  var l = document.createElementNS("http://www.w3.org/2000/svg", "path");
  l.socket = socket;
  l.down = true;
  l.tr = math.multiply(socket, 3);
  l.setAttribute("d", "M"+socket+" L"+l.tr);
  return l;
}

function spider() {
  var s = document.createElementNS("http://www.w3.org/2000/svg", "g");
  s.setAttribute("stroke", "black")
  var body = document.createElementNS("http://www.w3.org/2000/svg", "ellipse");
  body.setAttribute("rx", 3);
  body.setAttribute("ry", 2);
  s.appendChild(body);
  s.tr = [0,0];
  s.rot = [0];
  s.v = [0,1];
  for (socket of spiderLegSockets) {
    s.appendChild(spiderLeg(socket));
  }
  return s;
}

function updateSpider(s, dt) {
  s.tr = math.add(s.tr, [dt, 0]);
  s.setAttribute("transform", "translate("+s.tr+")");
  for (leg of s.children) {
    if (leg.tr) {
      if (leg.down) {
        leg.tr = math.add(leg.tr, [-dt, 0]);
      } else {
        leg.tr = math.add(leg.tr, [dt, 0]);
      }
      leg.setAttribute("d", "M"+leg.socket+" L"+leg.tr);
      if (leg.tr[0] > (leg.socket[0] > 0 ? 9 : 0)) {
        leg.down = true;
      } else if (leg.tr[0] < (leg.socket[0] > 0 ? 0 : -9)) {
        leg.down = false;
      }
    }
  }
}

var world = document.getElementById("world");

var lastTs = performance.now();

function animate(ts) {
  var dt = (ts - lastTs)/1000;
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
