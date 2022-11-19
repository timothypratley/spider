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
  var s = hiccup("g", {stroke: "black", "stroke-width": 0.5, "stroke-linecap": "round"});
  var bodyFront = hiccup("ellipse", {cx: 2, rx: 2, ry: 1});
  s.appendChild(bodyFront);
  var bodyBack = hiccup("ellipse", {cx: -2, rx: 3, ry: 2});
  s.appendChild(bodyBack);
  s.tr = [0,0];
  s.rotGoal = [0];
  s.rot = [0];
  s.urgency = 0.5;
  s.size = 0.5 + Math.random()/2;
  for (socket of spiderLegSockets) {
    s.appendChild(spiderLeg(socket));
  }
  return s;
}

function updateSpider(s, dt) {
  dt = dt * s.urgency;
  // stay in the world
  if (s.tr[0] < -60) {
    s.rotGoal = [0];
    s.rot = [0];
  } else if (s.tr[1] < -60) {
    s.rotGoal = [90];
    s.rot = [90];
  } else if (s.tr[0] > 60) {
    s.rotGoal = [180];
    s.rot = [180];
  } else if (s.tr[1] > 60) {
    s.rotGoal = [270];
    s.rot = [270];
  } else if (Math.random() < 0.01) {
    // randomly turn
    s.rotGoal = [Math.floor(Math.random()*360)];
  } else if (Math.random() < 0.01) {
    s.urgency = Math.random();
  }

  // rotate toward a goal
  let drot = 0;
  if (s.rotGoal[0] > s.rot[0] + 5) {
    drot = 5;
    dt *= 5;
  } else if (s.rotGoal[0] < s.rot[0] - 5) {
    drot = -5;
    dt *= 5;
  }
  s.rot[0] = s.rot[0] + drot;
  let rotr = Math.PI * s.rot[0] / 180;
  let rtr = math.rotate([dt, 0], rotr);
  s.tr = math.add(s.tr, rtr);
  s.setAttribute("transform", "translate("+s.tr+") rotate("+s.rot+") scale("+s.size+")");
  // TODO: feet shouldn't rotate when turning!
  for (leg of s.children) {
    if (leg.tr) {
      if (leg.down) {
        leg.tr = math.add(leg.tr, [-dt, 0]);
      } else {
        leg.tr = math.add(leg.tr, [4*dt, 0]);
      }
      if (leg.tr[0] > (leg.socket[0] > 0.5 ? 9 : -5)) {
        leg.down = true;
      } else if (leg.tr[0] < (leg.socket[0] > 0.5 ? 5 : -9)) {
        leg.down = false;
      }
      leg.setAttribute("d", "M"+leg.socket+" L"+leg.tr);
    }
  }
}

var world = document.getElementById("world");

var lastTs = performance.now();

function animate(ts) {
  var dt = Math.min(1, (ts - lastTs)/50);
  lastTs = ts;
  for (child of world.children) {
    updateSpider(child, dt);
  }
  requestAnimationFrame(animate);
}

function init() {
  for(i=0; i<50; i++) {
    world.appendChild(spider());
  }
  requestAnimationFrame(animate);
}

init();
