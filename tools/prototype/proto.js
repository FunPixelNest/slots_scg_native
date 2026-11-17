/* FunPixel Slots redesign prototype: our symbols and our 7 features in the new casino style. */
(function () {
"use strict";
var A = window.ASSETS;
var $ = function (s, r) { return (r || document).querySelector(s); };
var $$ = function (s, r) { return Array.prototype.slice.call((r || document).querySelectorAll(s)); };
function fmt(n) { return Math.round(n).toLocaleString("en-US"); }
function short(n) { return n >= 1e9 ? +(n / 1e9).toFixed(2) + "B" : n >= 1e6 ? +(n / 1e6).toFixed(2) + "M" : n >= 1e3 ? +(n / 1e3).toFixed(n >= 1e4 ? 0 : 1) + "K" : String(Math.round(n)); }
function rnd(a) { return a[Math.floor(Math.random() * a.length)]; }
function wait(ms) { return new Promise(function (r) { setTimeout(r, ms); }); }
function weighted(items) { var t = 0, i; for (i = 0; i < items.length; i++) t += items[i][1]; var r = Math.random() * t; for (i = 0; i < items.length; i++) { if (r < items[i][1]) return items[i][0]; r -= items[i][1]; } return items[items.length - 1][0]; }

/* ---------------- art drawn in code ---------------- */
var cid = 0;
function coinSVG(sz) { var g = "cg" + (++cid); return '<svg class="coinsvg" width="' + sz + '" height="' + sz + '" viewBox="0 0 40 40"><defs><radialGradient id="' + g + '" cx=".38" cy=".32" r=".75"><stop offset="0" stop-color="#fff6c8"/><stop offset=".55" stop-color="#ffd44a"/><stop offset="1" stop-color="#c8801a"/></radialGradient></defs><circle cx="20" cy="20" r="18" fill="url(#' + g + ')" stroke="#8a5208" stroke-width="2"/><circle cx="20" cy="20" r="12.5" fill="none" stroke="#b8740f" stroke-width="2"/><path d="M20 11l2.6 5.6 6.1.6-4.6 4 1.4 6-5.5-3.2-5.5 3.2 1.4-6-4.6-4 6.1-.6z" fill="#fff3b0" stroke="#b8740f" stroke-width="1"/></svg>'; }
var VID = '<svg viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="14" rx="3" fill="#0a3a12"/><path d="M10 9l5 3-5 3z" fill="#c6ff5a"/></svg>';
function chestSVG(sz, open) { return '<svg width="' + sz + '" height="' + sz + '" viewBox="0 0 100 100"><path d="M14 44h72v40a6 6 0 0 1-6 6H20a6 6 0 0 1-6-6z" fill="#8a4a1e" stroke="#3a1a06" stroke-width="3"/>' + (open ? '<path d="M14 44l6-30h60l6 30z" fill="#a85a26" stroke="#3a1a06" stroke-width="3"/><ellipse cx="50" cy="44" rx="30" ry="8" fill="#ffd44a"/><circle cx="38" cy="40" r="7" fill="#ffe27a" stroke="#b8740f" stroke-width="2"/><circle cx="56" cy="38" r="7" fill="#ffe27a" stroke="#b8740f" stroke-width="2"/>' : '<path d="M14 44c0-18 16-28 36-28s36 10 36 28z" fill="#a85a26" stroke="#3a1a06" stroke-width="3"/>') + '<rect x="14" y="44" width="72" height="9" fill="#ffc93c" stroke="#7a4a00" stroke-width="2"/><rect x="43" y="50" width="14" height="16" rx="3" fill="#ffd44a" stroke="#7a4a00" stroke-width="2"/></svg>'; }
var DIAMOND = '<svg viewBox="0 0 40 40" class="mark diamond"><defs><linearGradient id="dmg" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#e6fbff"/><stop offset=".5" stop-color="#38d6ff"/><stop offset="1" stop-color="#1f6fd0"/></linearGradient></defs><path d="M6 15l7-9h14l7 9-14 20z" fill="url(#dmg)" stroke="#fff" stroke-width="2"/><path d="M6 15h28M13 6l7 29 7-29" stroke="#fff" stroke-width="1.2" fill="none" opacity=".7"/></svg>';
var ICO = {
  tasks: '<svg width="38" height="38" viewBox="0 0 40 40"><rect x="7" y="4" width="26" height="32" rx="5" fill="#fff0a0" stroke="#8a4a00" stroke-width="2.5"/><path d="M13 14l3 3 6-6M13 25l3 3 6-6" stroke="#2fbf4a" stroke-width="3" fill="none" stroke-linecap="round"/><path d="M25 15h4M25 26h4" stroke="#8a4a00" stroke-width="3" stroke-linecap="round"/></svg>',
  gift: '<svg width="40" height="40" viewBox="0 0 40 40"><rect x="5" y="16" width="30" height="20" rx="3" fill="#ff5fb4" stroke="#8a0a4a" stroke-width="2"/><rect x="3" y="11" width="34" height="8" rx="2" fill="#ff8ad0" stroke="#8a0a4a" stroke-width="2"/><rect x="17" y="11" width="6" height="25" fill="#ffd34d"/><path d="M20 11c-6-9-14-4-8 0M20 11c6-9 14-4 8 0" stroke="#ffd34d" stroke-width="3" fill="none"/></svg>',
  piggy: '<svg width="42" height="40" viewBox="0 0 44 40"><ellipse cx="21" cy="22" rx="16" ry="12" fill="#ff8ab8" stroke="#a8285a" stroke-width="2"/><circle cx="37" cy="22" r="5" fill="#ff8ab8" stroke="#a8285a" stroke-width="2"/><path d="M12 13l3-8 6 6" fill="#ff8ab8" stroke="#a8285a" stroke-width="2"/><rect x="11" y="31" width="5" height="7" fill="#ff8ab8"/><rect x="25" y="31" width="5" height="7" fill="#ff8ab8"/><rect x="17" y="12" width="9" height="2.5" rx="1" fill="#a8285a"/><circle cx="28" cy="18" r="1.8" fill="#3a0a1a"/></svg>',
  target: '<svg width="38" height="38" viewBox="0 0 40 40"><circle cx="20" cy="20" r="16" fill="#e53950" stroke="#fff" stroke-width="2.5"/><circle cx="20" cy="20" r="10.5" fill="#fff"/><circle cx="20" cy="20" r="5.5" fill="#e53950"/><path d="M20 20L36 4" stroke="#ffd34d" stroke-width="3"/><path d="M31 3h6v6" stroke="#ffd34d" stroke-width="3" fill="none"/></svg>'
};
var PIG_BIG = '<svg width="190" height="160" viewBox="0 0 44 38"><ellipse cx="21" cy="21" rx="16" ry="12" fill="#ff8ab8" stroke="#a8285a" stroke-width="1.2"/><circle cx="37" cy="21" r="5" fill="#ff9ac2" stroke="#a8285a" stroke-width="1.2"/><circle cx="36" cy="20" r=".9" fill="#a8285a"/><circle cx="38.4" cy="20" r=".9" fill="#a8285a"/><path d="M12 12l3-8 6 6" fill="#ff8ab8" stroke="#a8285a" stroke-width="1.2"/><rect x="11" y="30" width="5" height="7" rx="1" fill="#ff8ab8" stroke="#a8285a" stroke-width="1"/><rect x="25" y="30" width="5" height="7" rx="1" fill="#ff8ab8" stroke="#a8285a" stroke-width="1"/><rect x="16" y="11" width="10" height="2" rx="1" fill="#a8285a"/><circle cx="29" cy="17" r="1.4" fill="#3a0a1a"/><ellipse cx="15" cy="16" rx="5" ry="2.5" fill="#fff" opacity=".35"/></svg>';

/* ---------------- game data ---------------- */
var FEATURE_OF = {
  pirates: "hs", gold_mine: "hs", egypt: "hs", china: "hs",
  christmas_eve: "fsm", valentines_day: "fsm", wolf: "fsm", viking: "fsm",
  neon: "cascade", candy: "cascade", tropical: "cascade",
  gems: "meter", india: "meter", arabian: "meter",
  farm: "coins", most_wanted: "coins", wild_west: "coins",
  modern: "wheel", egypt_2: "wheel", greek: "wheel",
  witch: "pick", forest: "pick", underwater: "pick"
};
var FEAT = {
  hs: { name: "Hold & Spin", demo: "pirates", jp: true },
  fsm: { name: "Free Spins Multiplier", demo: "christmas_eve" },
  cascade: { name: "Cascading Reels", demo: "neon" },
  meter: { name: "Diamond Collection", demo: "gems" },
  coins: { name: "Coin Collector", demo: "farm" },
  wheel: { name: "Bonus Wheel", demo: "modern", jp: true },
  pick: { name: "Pick Bonus", demo: "witch" }
};
/* unlocks: 2 free, then video/level machines at levels 5,10,15,20,30,...,100 (as in the game) */
var RULES = [null, null, ["v", 5], ["l", 5], ["l", 10], ["v", 15], ["l", 15], ["v", 20], ["l", 20], ["v", 30], ["l", 30], ["l", 40], ["v", 50], ["l", 50], ["v", 60], ["l", 60], ["v", 70], ["l", 70], ["l", 80], ["v", 90], ["l", 90], ["v", 100], ["l", 100]];
var BETS = [2500, 5000, 10000, 25000, 50000, 100000, 250000];
var JP = [["MINI", 10, "c-blue"], ["MINOR", 40, "c-green"], ["MAJOR", 200, "c-pink"], ["GRAND", 1000, "c-gold"]];
var FS_LADDER = [1, 2, 3, 5, 10], CASCADE_LADDER = [1, 2, 3, 5];
var METER_TARGET = 50;
var LUCKY = [62500, 125000, 93750, 312500, 187500, 625000, 75000, 1250000];
var LUCKY_W = [22, 20, 20, 10, 14, 6, 22, 2];
var DAILY = [100000, 150000, 200000, 300000, 400000, 600000, 1000000];
var TASKS = [["Spin 50 times", 50, 32, 10000], ["Win on 15 spins", 15, 15, 15000], ["Win 500K coins", 500000, 180000, 20000], ["Spin 10 times at Max bet", 10, 0, 25000], ["Land a Big Win", 1, 0, 30000]];
var MISSIONS = [["Play 3 different machines", 3, 3, 30000], ["Reach level 5", 5, 6, 40000], ["Spin 500 times", 500, 220, 50000], ["Trigger 5 bonus games", 5, 1, 60000], ["Reach level 10", 10, 6, 70000], ["Land 10 Big Wins", 10, 2, 80000], ["Play 10 different machines", 10, 4, 90000], ["Win 50M coins in total", 50000000, 4200000, 100000]];

var LOBBY = A.lobby.map(function (e) { var f = FEATURE_OF[e.id]; return { id: e.id, name: e.name, order: e.order, cover: e.cover, f: f, rule: RULES[e.order] }; });
var byId = {}; LOBBY.forEach(function (t) { byId[t.id] = t; });

var S = {
  coins: 12500000, level: 6, xp: 40, bi: 0, theme: null, spinning: false, auto: 0, fs: null,
  meter: { gems: 34 }, lastCollect: 0, piggy: 1350000, cat: "all", watched: {}, demoOpen: {},
  dailyDay: 4, dailyClaimed: false, luckyUsed: false, avatar: 0, name: "Lucky Player", sound: false,
  qClaimed: {}, stats: { spins: 1248, wins: 512, biggest: 2480000, won: 38450000, big: 17, bonus: 9 }, grid: null
};
function bet() { return BETS[S.bi]; }
function unlockedSteps() { return Math.min(BETS.length, 5 + Math.floor((S.level - 1) / 2)); }

/* ---------------- sound (after first tap) ---------------- */
var actx = null;
function tone(f, d, type, v, delay) { if (!S.sound) return; try { actx = actx || new (window.AudioContext || window.webkitAudioContext)(); var t = actx.currentTime + (delay || 0); var o = actx.createOscillator(), g = actx.createGain(); o.type = type || "sine"; o.frequency.value = f; g.gain.setValueAtTime(v || .05, t); g.gain.exponentialRampToValueAtTime(.0001, t + d); o.connect(g); g.connect(actx.destination); o.start(t); o.stop(t + d); } catch (e) {} }
function chime() { [523, 659, 784, 1046].forEach(function (f, i) { tone(f, .18, "triangle", .05, i * .09); }); }

/* ---------------- screens & overlays ---------------- */
var screen = "splash";
function show(id) { screen = id; $$(".scr").forEach(function (s) { s.classList.toggle("show", s.id === id); }); closeAll(); renderCoins(); }
function openOv(id) { $("#" + id).classList.add("show"); }
function closeOv(id) { $("#" + id).classList.remove("show"); }
function closeAll() { $$(".ov").forEach(function (o) { o.classList.remove("show"); }); }
var toastT;
function toast(m, ms) { var t = $("#toast"); t.textContent = m; t.classList.add("show"); clearTimeout(toastT); toastT = setTimeout(function () { t.classList.remove("show"); }, ms || 2400); }
function renderCoins(bump) { $$("[data-coins] b").forEach(function (e) { e.textContent = fmt(S.coins); }); if (bump) $$("[data-coins]").forEach(function (e) { e.classList.remove("bump"); void e.offsetWidth; e.classList.add("bump"); }); }
function renderLevel() { $$("[data-lv]").forEach(function (e) { e.textContent = S.level; }); $$(".lvl .bar").forEach(function (e) { e.style.setProperty("--x", S.xp + "%"); }); $$("[data-avatar]").forEach(function (e) { e.src = A.avatars[S.avatar]; }); }
function addCoins(n) { S.coins += n; renderCoins(true); }
function scale() { var p = $("#phone"); var m = /scale\(([\d.]+)\)/.exec(p.style.transform || ""); return m ? +m[1] : 1; }
function relRect(el) { var a = $("#app").getBoundingClientRect(), r = el.getBoundingClientRect(), s = scale(); return { x: (r.left - a.left) / s, y: (r.top - a.top) / s, w: r.width / s, h: r.height / s }; }
function fly(fromEl, toEl, html, n, done) {
  var f = relRect(fromEl), t = relRect(toEl), app = $("#app"); n = n || 6;
  for (var i = 0; i < n; i++) (function (i) {
    var d = document.createElement("div"); d.className = "fly"; d.innerHTML = html;
    d.style.left = (f.x + f.w / 2 - 15 + (Math.random() * 30 - 15)) + "px"; d.style.top = (f.y + f.h / 2 - 15) + "px"; app.appendChild(d);
    setTimeout(function () { d.style.transform = "translate(" + (t.x + t.w / 2 - (f.x + f.w / 2)) + "px," + (t.y + t.h / 2 - (f.y + f.h / 2)) + "px) scale(.7)"; d.style.opacity = "0"; }, 30 + i * 60);
    setTimeout(function () { d.remove(); if (i === n - 1 && done) done(); }, 900 + i * 60);
  })(i);
}
function rain(el, n) { el.innerHTML = ""; for (var i = 0; i < (n || 26); i++) { var s = document.createElement("span"); s.innerHTML = coinSVG(26 + Math.random() * 14); s.style.left = Math.random() * 100 + "%"; s.style.animationDuration = (1.6 + Math.random() * 1.6) + "s"; s.style.animationDelay = (Math.random() * .8) + "s"; el.appendChild(s); } }
function countUp(el, to, ms) { var from = 0, t0 = performance.now(); return new Promise(function (res) { (function step(t) { var k = Math.min(1, (t - t0) / ms); el.textContent = fmt(from + (to - from) * (1 - Math.pow(1 - k, 3))); if (k < 1) requestAnimationFrame(step); else res(); })(t0); }); }
function banner(text, ms) { var b = document.createElement("div"); b.className = "banner gold-t"; b.style.color = "#fff"; b.textContent = text; $("#game").appendChild(b); return wait(ms || 1000).then(function () { b.remove(); }); }

/* generic reward popup with optional x2 video */
var ppDone = null;
function popup(o) {
  $("#ppTag").textContent = o.tag || ""; $("#ppTitle").textContent = o.title || ""; $("#ppSub").textContent = o.sub || "";
  $("#ppArt").innerHTML = o.art || coinSVG(90); $("#ppAmt").textContent = o.amt ? fmt(o.amt) : "";
  $("#ppOk").textContent = o.btn || (o.amt ? "COLLECT" : "OK");
  $("#ppX2").hidden = !(o.amt && o.x2 !== false);
  ppDone = function (mult) { closeOv("popup"); if (o.amt && o.grant !== false) addCoins(o.amt * mult); if (o.done) o.done(); };
  if (o.amt) rain($("#rain2"), 18); else $("#rain2").innerHTML = "";
  openOv("popup"); chime();
}
function watchVideo(cb) { toast("▶ Rewarded video plays here (demo)", 1200); setTimeout(cb, 1100); }

/* ---------------- splash ---------------- */
var splashT;
function runSplash() {
  show("splash"); var p = 0; $("#loadfill").style.width = "0"; clearInterval(splashT);
  splashT = setInterval(function () { p = Math.min(100, p + 4 + Math.random() * 7); $("#loadfill").style.width = p + "%"; $("#loadtx").textContent = "Loading " + Math.round(p) + "%"; if (p >= 100) { clearInterval(splashT); if (!tourHold) setTimeout(function () { show("lobby"); }, 400); } }, 120);
}

/* ---------------- lobby ---------------- */
var CATS = [["all", "All"], ["jp", "Jackpots"], ["fsm", "Free Spins"], ["cascade", "Cascade"], ["collect", "Collect"], ["bonus", "Bonus"]];
function isOpen(t) { var r = t.rule; if (!r || S.demoOpen[t.id]) return true; if (r[0] === "l") return S.level >= r[1]; return !!S.watched[t.id] || S.level >= r[1]; }
function catOk(t) { switch (S.cat) { case "jp": return FEAT[t.f].jp; case "fsm": return t.f === "fsm"; case "cascade": return t.f === "cascade"; case "collect": return t.f === "meter" || t.f === "coins"; case "bonus": return t.f === "hs" || t.f === "wheel" || t.f === "pick"; default: return true; } }
function renderCats() { $("#cats").innerHTML = CATS.map(function (c) { return '<button class="cat' + (S.cat === c[0] ? " on" : "") + '" data-cat="' + c[0] + '">' + c[1] + "</button>"; }).join(""); }
function flagOf(t) { if (t.order < 2) return '<span class="flag hot">HOT</span>'; if (FEAT[t.f].jp) return '<span class="flag jp">JACKPOT</span>'; if (t.f === "cascade" || t.f === "meter") return '<span class="flag new">NEW</span>'; return ""; }
function renderTiles() {
  $("#tiles").innerHTML = LOBBY.filter(catOk).map(function (t) {
    var open = isOpen(t), lock = "";
    if (!open) { var r = t.rule; lock = '<span class="lock"><span class="pad"><svg viewBox="0 0 24 24"><rect x="5" y="10" width="14" height="11" rx="2" fill="#5a2a00"/><path d="M8 10V7a4 4 0 0 1 8 0v3" stroke="#5a2a00" stroke-width="2.6" fill="none"/></svg></span><b>' + (r[0] === "l" ? "LEVEL " + r[1] : "▶ WATCH VIDEO") + "</b>" + (r[0] === "v" ? "<small>or reach level " + r[1] + "</small>" : "") + "</span>"; }
    return '<button class="tile" data-tile="' + t.id + '"><img class="art" src="' + t.cover + '" alt="' + t.name + '">' + flagOf(t) + '<span class="feat">' + FEAT[t.f].name.toUpperCase() + "</span>" + lock + "</button>";
  }).join("");
}
var HEROES = ["pirates", "christmas_eve", "neon", "gems", "farm"];
var HERO_TXT = { pirates: "Coins lock and respins reset. Fill all 15 for the GRAND.", christmas_eve: "Every winning free spin raises the multiplier to x10.", neon: "Wins burst, new symbols fall, multiplier climbs.", gems: "Fill the diamond meter for 8 Super Free Spins.", farm: "The collector grabs every coin on the reels." };
var hi = 0, heroT;
function renderHero() {
  $("#htrack").innerHTML = HEROES.map(function (id) { var t = byId[id], th = A.themes[id]; return '<div class="hslide" style="background-image:url(' + th.bg + ')"><span class="shade"></span><img class="ch" src="' + t.cover + '" alt=""><span class="tx"><span><span class="tag">' + FEAT[t.f].name.toUpperCase() + "</span><h3>" + t.name + "</h3><p>" + HERO_TXT[id] + '</p></span><button class="pbtn" data-play="' + id + '">PLAY</button></span></div>'; }).join("");
  $("#heroDots").innerHTML = HEROES.map(function (_, i) { return '<i data-hd="' + i + '"' + (i === hi ? ' class="on"' : "") + "></i>"; }).join("");
}
function heroGo(i) { hi = (i + HEROES.length) % HEROES.length; $("#htrack").style.transform = "translateX(" + (-hi * 100) + "%)"; $$("#heroDots i").forEach(function (d, k) { d.classList.toggle("on", k === hi); }); }
function openTile(id) {
  var t = byId[id]; if (!t) return;
  if (!isOpen(t)) { showUnlock(id); return; }
  var play = A.themes[id] ? id : FEAT[t.f].demo;
  if (play !== id) toast(t.name + " plays like " + byId[play].name + " in this demo", 2200);
  startGame(play);
}
var ulkId = null;
function showUnlock(id) {
  ulkId = id; var t = byId[id], r = t.rule;
  $("#ulkImg").src = t.cover; $("#ulkName").textContent = t.name;
  $("#ulkRule").innerHTML = r[0] === "l" ? "Unlocks at <b>Level " + r[1] + "</b> · you are level " + S.level : "Watch one short video, or reach <b>Level " + r[1] + "</b>";
  $("#ulkBar").style.width = Math.min(100, S.level / r[1] * 100) + "%";
  $("#ulkOr").textContent = r[0] === "l" ? "— DEMO ONLY —" : "— OR UNLOCK NOW —";
  $("#ulkWatch").innerHTML = r[0] === "l" ? "OPEN FOR THE DEMO" : VID + "WATCH VIDEO";
  openOv("unlock");
}

/* ---------------- store ---------------- */
var PACKS = [["Lucky Pouch", 1000000, 1000000, "$0.99", ""], ["Golden Stack", 2600000, 2000000, "$1.99", ""], ["Treasure Bowl", 7500000, 5000000, "$4.99", "POPULAR"], ["Jackpot Case", 18000000, 10000000, "$9.99", ""], ["Royal Chest", 50000000, 25000000, "$19.99", "BEST VALUE"], ["Millionaire Vault", 120000000, 50000000, "$39.99", "HOT"]];
var stab = "offers";
function renderStore() {
  $$("#stabs button").forEach(function (b) { b.classList.toggle("on", b.dataset.st === stab); });
  var h = "";
  if (stab === "offers") {
    h += '<div class="soffer" style="min-height:150px"><span class="sun"></span><div class="so-tx"><span class="so-tag">WELCOME OFFER</span><b class="gold-t">10,000,000</b><small>COINS · +300% MORE THAN 2.5M</small><span class="otimer" style="position:static">⏱ <span data-timer>23:59:41</span></span><button class="price" data-buy="10000000" style="margin-top:8px">BUY $4.99</button></div><span style="position:relative">' + chestSVG(110, true) + "</span></div>";
    h += '<div class="soffer" style="background:linear-gradient(135deg,#2bd9ff,#1a3aa8)"><div class="so-tx"><span class="so-tag">PIGGY BANK</span><b class="gold-t">' + fmt(S.piggy) + '</b><small>SAVED FROM YOUR SPINS</small></div><button class="price" data-open="piggy">OPEN</button></div>';
    h += '<button class="sfree" data-ad="100000"><svg width="34" height="34" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="14" rx="3" fill="#0a3a12"/><path d="M10 9l5 3-5 3z" fill="#c6ff5a"/></svg><span><b>FREE COINS</b><small>Watch a short video · every 4 hours</small></span><em>+100K</em></button>';
  } else {
    h += '<div class="sgrid">' + PACKS.map(function (p) { var more = Math.round((p[1] / p[2] - 1) * 100); return '<div class="spack"><h4>' + p[0] + "</h4>" + (p[4] ? '<span class="srib">' + p[4] + "</span>" : "") + '<div class="sbody">' + chestSVG(70, true) + "<b>" + fmt(p[1]) + "</b>" + (more ? "<small>+" + more + "% MORE</small>" : "<small>&nbsp;</small>") + '</div><button class="price" data-buy="' + p[1] + '">' + p[3] + "</button></div>"; }).join("") + "</div>";
  }
  h += '<p class="sfine">Virtual coins for fun only · no cash value · prices are placeholders</p>';
  $("#sscroll").innerHTML = h;
}

/* ---------------- slot engine ---------------- */
var T = null; // current theme
function kindOf(i) { return i === T.wild ? "wild" : i === T.scatter ? "scatter" : i < 4 ? "low" : "high"; }
var PAY = { low: { 3: 1, 4: 2, 5: 5 }, high: { 3: 3, 4: 8, 5: 20 } }, UNIT = 20;
function genGrid() {
  var g = [];
  for (var r = 0; r < 5; r++) {
    var col = [], sc = false;
    for (var row = 0; row < 3; row++) {
      var items = [[0, 10], [1, 10], [2, 10], [3, 10], [4, 6], [5, 6], [6, 6], [7, 6]];
      if (r > 0) items.push([T.wild, 2.2]);
      if (T.scatter >= 0 && !sc) items.push([T.scatter, S.fs ? 1.2 : 1.7]);
      var s = weighted(items); if (s === T.scatter) sc = true; col.push(s);
    }
    g.push(col);
  }
  return g;
}
function evaluate(g, b) {
  var total = 0, cells = {}, best = 0;
  for (var s = 0; s < 8; s++) {
    var n = 0, ways = 1, hit = [];
    for (var r = 0; r < 5; r++) {
      var c = 0; for (var row = 0; row < 3; row++) if (g[r][row] === s || (r > 0 && g[r][row] === T.wild)) { c++; hit.push(r + ":" + row); }
      if (!c) break; n++; ways *= c;
    }
    if (n >= 3) { var p = PAY[kindOf(s)][n] * ways * b / UNIT; total += p; hit.forEach(function (k) { if (+k.split(":")[0] < n) cells[k] = 1; }); best = Math.max(best, n); }
  }
  var sc = []; for (var r2 = 0; r2 < 5; r2++) for (var w = 0; w < 3; w++) if (g[r2][w] === T.scatter) sc.push(r2 + ":" + w);
  return { total: Math.round(total), cells: Object.keys(cells), scatters: sc };
}

/* ---------------- game screen ---------------- */
function startGame(id) {
  T = A.themes[id]; T.id = id; T.f = FEATURE_OF[id]; S.theme = id; S.fs = null; S.auto = 0; S.spinning = false;
  $("#gbg").style.backgroundImage = "url(" + T.bg + ")";
  $("#tname").textContent = byId[id].name.toUpperCase();
  show("game"); S.grid = genGrid(); renderGrid(S.grid); renderFeat(); renderBet(); updSpin(); clearWin();
  $("#winlbl").textContent = "243 WAYS · GOOD LUCK!"; $("#winamt").textContent = "";
}
function cellHTML(s) { return '<div class="cell"><img src="' + T.sym[s] + '" alt=""></div>'; }
function renderGrid(g, dropCells) {
  $("#reels").innerHTML = g.map(function (col, r) { return '<div class="reel" data-r="' + r + '"><div class="strip">' + col.map(function (s, row) { return '<div class="cell' + (dropCells && dropCells[r + ":" + row] ? " drop" : "") + '"><img src="' + T.sym[s] + '" alt=""></div>'; }).join("") + "</div></div>"; }).join("");
}
function cellEl(r, row) { var reel = $('.reel[data-r="' + r + '"]'); return reel ? reel.querySelectorAll(".cell")[row] : null; }
function clearWin() { $$(".cell.win,.cell.dim").forEach(function (c) { c.classList.remove("win", "dim"); }); $("#winbar").classList.remove("hit"); }
function renderBet() { $("#betTx").textContent = short(bet()); $("#betDn").disabled = S.bi === 0 || !!S.fs; $("#betUp").disabled = S.bi >= unlockedSteps() - 1 || !!S.fs; }
function updSpin() {
  var b = $("#spinBtn"); b.classList.toggle("busy", S.spinning); b.classList.toggle("free", !!S.fs); b.classList.toggle("auto", S.auto > 0 && !S.fs);
  $("#spinTx").textContent = S.fs ? "FREE" : S.auto > 0 ? "STOP" : "SPIN";
  $("#spinSub").textContent = S.fs ? S.fs.left + " LEFT" : S.auto > 0 ? S.auto + " AUTO" : "HOLD FOR AUTO";
  $("#autoTx").textContent = S.auto > 0 ? "STOP " + S.auto : "AUTO"; $("#autoBtn").classList.toggle("on", S.auto > 0);
}
function jpVal(k) { return bet() * JP[k][1]; }
function renderFeat() {
  var f = T.f, h = "";
  if (f === "hs" || f === "wheel") {
    h = '<span class="fh">' + (f === "hs" ? "HOLD & SPIN" : "BONUS WHEEL") + ' · JACKPOTS</span><div class="frow">' + JP.map(function (j, k) { return '<div class="fpill ' + j[2] + '"><small>' + j[0] + "</small><b>" + short(jpVal(k)) + "</b></div>"; }).join("") + '</div><div class="fnote">4+ ' + (f === "hs" ? "scatters start Hold & Spin · fill all 15 spots for the GRAND" : "scatters spin the Bonus Wheel · a slice can be a jackpot") + "</div>";
  } else if (f === "fsm") {
    var lit = S.fs ? S.fs.ladder : -1;
    h = '<span class="fh">' + (S.fs ? "FREE SPINS · " + S.fs.left + " LEFT" : "FREE SPINS MULTIPLIER") + '</span><div class="frow">' + FS_LADDER.map(function (m, i) { return '<div class="fpill ' + ["c-purple", "c-blue", "c-green", "c-orange", "c-gold"][i] + (i === lit ? " lit" : "") + '"><small>' + (i ? "STEP " + i : "START") + "</small><b>x" + m + "</b></div>"; }).join("") + '</div><div class="fnote">' + (S.fs ? "Win to climb the ladder · free-spin win " + fmt(S.fs.win) : "3 scatters give 8 free spins · each winning free spin raises the multiplier") + "</div>";
  } else if (f === "cascade") {
    var cl = S.cascadeStep == null ? -1 : S.cascadeStep;
    h = '<span class="fh">CASCADING REELS</span><div class="frow">' + CASCADE_LADDER.map(function (m, i) { return '<div class="fpill ' + ["c-purple", "c-blue", "c-green", "c-orange"][i] + (i === cl ? " lit" : "") + '"><small>' + (i ? "CASCADE " + i : "WIN") + "</small><b>x" + m + "</b></div>"; }).join("") + '</div><div class="fnote">Winning symbols burst, new ones fall in, and every cascade raises the multiplier</div>';
  } else if (f === "meter") {
    var m = S.meter.gems || 0;
    h = '<span class="fh">' + (S.fs ? "SUPER FREE SPINS · " + S.fs.left + " LEFT · x2" : "DIAMOND COLLECTION") + '</span><div class="meter" id="meterEl"><span style="width:' + (m / METER_TARGET * 100) + '%"></span><em>◆ ' + m + " / " + METER_TARGET + '</em></div><div class="fnote">Diamonds land on the reels and fly into the meter · a full meter gives 8 Super Free Spins at x2</div>';
  } else if (f === "coins") {
    h = '<span class="fh">COIN COLLECTOR</span><div class="fbig" id="collectEl">' + coinSVG(38) + "<b class=\"gold-t\">" + (S.lastCollect ? fmt(S.lastCollect) : "COLLECT!") + "</b><small>Coins land on reels 1–4 · the COLLECTOR on reel 5 grabs them all</small></div>";
  } else if (f === "pick") {
    h = '<span class="fh">PICK BONUS</span><div class="fbig">' + chestSVG(46, false) + chestSVG(46, true) + chestSVG(46, false) + '<small>4+ scatters open 9 treasure chests · pick 3 and win what is inside</small></div>';
  }
  $("#feat").innerHTML = h;
}

/* ---------------- spinning ---------------- */
function feedPiggy(b) { var cap = 2000000 + 500000 * (S.level - 1); S.piggy = Math.min(cap, S.piggy + Math.round(b * .03)); }
function doSpin(forced) {
  if (S.spinning || screen !== "game" || $$(".ov.show").length) return;
  var free = !!S.fs;
  if (!free) {
    if (S.coins < bet()) { S.auto = 0; updSpin(); toast("Not enough coins · lower the bet or visit the store"); return; }
    S.coins -= bet(); renderCoins(); feedPiggy(bet());
  } else { S.fs.left--; }
  S.spinning = true; S.cascadeStep = null; clearWin(); $("#winamt").textContent = ""; $("#winlbl").textContent = free ? "FREE SPIN" : "GOOD LUCK!";
  updSpin(); renderFeat(); tone(220, .12, "square", .03);
  var g = forced && forced.grid ? forced.grid.map(function (c) { return c.slice(); }) : genGrid();
  var marks = rollMarks(g, forced);
  animateReels(g).then(function () { S.grid = g; return resolve(g, marks, free, forced || {}); }).then(function () {
    S.spinning = false; S.stats.spins++; gainXp(); updSpin(); renderFeat(); renderBet();
    if (S.fs && S.fs.left === 0) return finishFs();
    if ((S.fs || S.auto > 0) && !$$(".ov.show").length) { if (S.auto > 0 && !S.fs) S.auto--; updSpin(); setTimeout(function () { doSpin(); }, 650); }
  });
}
function animateReels(g) {
  var H = 82;
  return Promise.all(g.map(function (col, r) {
    return new Promise(function (res) {
      var reel = $('.reel[data-r="' + r + '"]'), strip = reel.querySelector(".strip"), extra = 16 + r * 3, h = "";
      col.forEach(function (s) { h += cellHTML(s); });
      for (var i = 0; i < extra; i++) h += cellHTML(rnd([0, 1, 2, 3, 4, 5, 6, 7]));
      strip.innerHTML = h; strip.style.transition = "none"; strip.style.transform = "translateY(" + (-extra * H) + "px)"; strip.classList.add("blur");
      void strip.offsetHeight;
      var d = 700 + r * 220;
      strip.style.transition = "transform " + d + "ms cubic-bezier(.25,.85,.35,1.06)"; strip.style.transform = "translateY(0)";
      setTimeout(function () { strip.classList.remove("blur"); }, d - 180);
      setTimeout(function () { strip.style.transition = "none"; strip.innerHTML = col.map(cellHTML).join(""); tone(330 + r * 40, .06, "triangle", .04); res(); }, d + 30);
    });
  }));
}
function rollMarks(g, forced) {
  var m = { coins: {}, collector: null, diamonds: [] };
  if (T.f === "coins") {
    if (forced && forced.coins) { m.coins = forced.coins; m.collector = forced.collector; }
    else { for (var r = 0; r < 4; r++) for (var row = 0; row < 3; row++) if (g[r][row] !== T.scatter && Math.random() < .06) m.coins[r + ":" + row] = bet() * weighted([[.5, 40], [1, 30], [2, 15], [5, 10], [10, 4], [25, 1]]); if (Math.random() < .14) m.collector = "4:" + Math.floor(Math.random() * 3); }
  }
  if (T.f === "meter") {
    if (forced && forced.diamonds) m.diamonds = forced.diamonds;
    else { var n = Math.random() < .07 ? 2 : Math.random() < .38 ? 1 : 0; var all = []; for (var r2 = 0; r2 < 5; r2++) for (var w = 0; w < 3; w++) all.push(r2 + ":" + w); all.sort(function () { return Math.random() - .5; }); m.diamonds = all.slice(0, n); }
  }
  return m;
}
function placeMarks(m) {
  Object.keys(m.coins).forEach(function (k) { var p = k.split(":"), c = cellEl(+p[0], +p[1]); if (c) c.insertAdjacentHTML("beforeend", '<span class="mark coin">' + short(m.coins[k]) + "</span>"); });
  if (m.collector) { var p = m.collector.split(":"), c = cellEl(+p[0], +p[1]); if (c) c.insertAdjacentHTML("beforeend", '<span class="mark collect">COLLECT</span>'); }
  m.diamonds.forEach(function (k) { var p = k.split(":"), c = cellEl(+p[0], +p[1]); if (c) c.insertAdjacentHTML("beforeend", DIAMOND); });
}
function highlight(cells) { if (!cells.length) return; $$(".cell").forEach(function (c) { c.classList.add("dim"); }); cells.forEach(function (k) { var p = k.split(":"), c = cellEl(+p[0], +p[1]); if (c) { c.classList.remove("dim"); c.classList.add("win"); } }); }

async function resolve(g, marks, free, forced) {
  placeMarks(marks);
  var win = 0, res;
  if (T.f === "cascade") {
    win = await runCascade(g);
    res = evaluate(S.grid, bet());
  } else {
    res = evaluate(g, bet());
    win = res.total;
    if (S.fs) { var mult = S.fs.kind === "super" ? 2 : FS_LADDER[S.fs.ladder]; win *= mult; if (mult > 1 && win > 0) await banner("x" + mult + " MULTIPLIER", 800); }
    highlight(res.cells);
  }
  // coin collector
  if (T.f === "coins" && marks.collector && Object.keys(marks.coins).length) {
    var sum = 0; Object.keys(marks.coins).forEach(function (k) { sum += marks.coins[k]; });
    await banner("COIN COLLECT!", 900);
    var target = $("#collectEl") || $("#feat");
    await new Promise(function (r) { var ks = Object.keys(marks.coins); ks.forEach(function (k, i) { var p = k.split(":"); fly(cellEl(+p[0], +p[1]), target, coinSVG(28), 2, i === ks.length - 1 ? r : null); }); });
    S.lastCollect = sum; win += sum; renderFeat();
  }
  // diamonds
  if (T.f === "meter" && marks.diamonds.length) {
    await new Promise(function (r) { marks.diamonds.forEach(function (k, i) { var p = k.split(":"); fly(cellEl(+p[0], +p[1]), $("#meterEl"), DIAMOND.replace('class="mark diamond"', 'width="28" height="28"'), 1, i === marks.diamonds.length - 1 ? r : null); }); });
    S.meter.gems += marks.diamonds.length; renderFeat();
    if (S.meter.gems >= METER_TARGET) {
      S.meter.gems -= METER_TARGET; renderFeat(); S.stats.bonus++;
      await showPop({ tag: "DIAMOND METER FULL", art: DIAMOND.replace('class="mark diamond"', 'width="110" height="110"'), title: "8 SUPER FREE SPINS!", sub: "Every win in these spins is doubled", btn: "START", x2: false });
      S.fs = S.fs ? (S.fs.left += 8, S.fs) : { left: 8, total: 8, win: 0, ladder: 0, kind: "super" };
    }
  }
  if (S.fs) S.fs.win += win;
  if (win > 0) { S.stats.wins++; S.stats.won += win; await presentWin(win); }
  else $("#winlbl").textContent = "SO CLOSE!";
  if (S.fs && T.f === "fsm" && win > 0) { S.fs.ladder = Math.min(FS_LADDER.length - 1, S.fs.ladder + 1); renderFeat(); }
  // scatter features
  var nSc = (T.f === "cascade" ? evaluate(g, bet()) : res).scatters.length;
  if (T.f === "fsm" && nSc >= 3) {
    var spins = 8; S.stats.bonus++;
    await showPop({ tag: nSc + " SCATTERS", art: '<img src="' + T.sym[T.scatter] + '" width="110" alt="">', title: spins + " FREE SPINS!", sub: "Each winning free spin raises the multiplier: x1 → x2 → x3 → x5 → x10", btn: "START", x2: false });
    if (S.fs) S.fs.left += spins; else S.fs = { left: spins, total: spins, win: 0, ladder: 0, kind: "mult" };
  }
  if (nSc >= 4 && !free) {
    S.stats.bonus++;
    if (T.f === "hs") await runHoldSpin(res.scatters);
    if (T.f === "wheel") await runBonusWheel();
    if (T.f === "pick") await runPick();
  }
}
async function runCascade(g) {
  var total = 0, step = 0, grid = g.map(function (c) { return c.slice(); });
  while (step < 8) {
    var res = evaluate(grid, bet());
    if (!res.total) break;
    var m = CASCADE_LADDER[Math.min(step, CASCADE_LADDER.length - 1)];
    S.cascadeStep = Math.min(step, CASCADE_LADDER.length - 1); renderFeat();
    highlight(res.cells);
    var stepWin = Math.round(res.total * .55) * m; total += stepWin;
    $("#winlbl").textContent = step ? "CASCADE x" + m : "WIN"; $("#winamt").textContent = fmt(total); $("#winbar").classList.add("hit");
    if (step) { tone(660 + step * 80, .2, "triangle", .05); banner("CASCADE x" + m, 800); }
    await wait(950);
    res.cells.forEach(function (k) { var p = k.split(":"), c = cellEl(+p[0], +p[1]); if (c) c.classList.add("burst"); });
    await wait(320);
    var dropped = {};
    grid = grid.map(function (col, r) {
      var keep = col.filter(function (_, row) { return res.cells.indexOf(r + ":" + row) < 0; }), add = 3 - keep.length;
      var fresh = []; for (var i = 0; i < add; i++) fresh.push(rnd([0, 1, 2, 3, 4, 5, 6, 7, T.wild]));
      var out = fresh.concat(keep); if (add) for (var j = 0; j < 3; j++) if (j < add + 2) dropped[r + ":" + j] = 1;
      return out;
    });
    renderGrid(grid, dropped); S.grid = grid; await wait(450); step++;
  }
  S.grid = grid; S.cascadeStep = step ? Math.min(step - 1, 3) : null; renderFeat();
  return total;
}
function presentWin(win) {
  $("#winbar").classList.add("hit"); $("#winlbl").textContent = "WIN"; chime();
  S.stats.biggest = Math.max(S.stats.biggest, win);
  var x = win / bet();
  if (x >= 10) { S.stats.big++; return bigWin(win, x >= 50 ? "HUGE WIN!" : x >= 25 ? "MEGA WIN!" : "BIG WIN!"); }
  addCoins(win); return countUp($("#winamt"), win, 700);
}
var bwDone = null;
function bigWin(win, title) {
  return new Promise(function (resolve) {
    $("#bwTitle").textContent = title; $("#bwAmt").textContent = "0"; $("#bwDouble").hidden = !!S.fs; rain($("#rain"), 30); openOv("bigwin"); chime();
    countUp($("#bwAmt"), win, 1600);
    bwDone = function (mult) { closeOv("bigwin"); addCoins(win * mult); $("#winamt").textContent = fmt(win * mult); fly($("#bwAmt"), $("[data-coins]", $("#game")), coinSVG(28), 8); resolve(); };
  });
}
function showPop(o) { return new Promise(function (res) { var d = o.done; o.done = function () { if (d) d(); res(); }; popup(o); }); }
async function finishFs() {
  var fs = S.fs; S.fs = null; updSpin(); renderFeat(); renderBet();
  await showPop({ tag: "FREE SPINS COMPLETE", title: fs.total + " FREE SPINS WON", sub: "Total " + fmt(fs.win) + " coins, already in your balance", btn: "GREAT!" });
}
function gainXp() {
  S.xp += (S.bi + 1) * 6; if (S.xp < 100) { renderLevel(); return; }
  S.xp -= 100; S.level++; renderLevel(); renderBet();
  var reward = 50000 * S.level;
  $("#lvNum").textContent = S.level; $("#lvAmt").textContent = fmt(reward);
  $("#lvSub").textContent = unlockedSteps() > BETS.indexOf(bet()) ? "Higher bets are now available." : "New machines unlock as you level up.";
  rain($("#rain3"), 18); openOv("lvup"); chime(); lvReward = reward;
}
var lvReward = 0;

/* ---------------- bonus games ---------------- */
function runHoldSpin(startCells) {
  return new Promise(function (resolve) {
    var cells = new Array(15).fill(null), respins = 3, total = 0, b = bet();
    function val() { var v = weighted([[1, 22], [2, 20], [3, 16], [5, 12], [8, 8], [10, 6], ["MINI", 5], ["MINOR", 2]]); return typeof v === "number" ? { c: v * b, t: short(v * b) } : { c: jpVal(v === "MINI" ? 0 : 1), t: v, jp: 1 }; }
    startCells.forEach(function (k) { var p = k.split(":"); cells[+p[1] * 5 + +p[0]] = val(); });
    function draw(fresh) {
      $("#hsGrid").innerHTML = cells.map(function (c, i) { return '<div class="hsc' + (c ? " on" + (c.jp ? " jp" : "") : "") + '"' + (fresh && fresh[i] ? "" : ' style="animation:none"') + ">" + (c ? c.t : "") + "</div>"; }).join("");
      total = cells.reduce(function (a, c) { return a + (c ? c.c : 0); }, 0);
      $("#hsTotal").textContent = fmt(total);
      $$("#hsRespins i").forEach(function (e, i) { e.classList.toggle("on", i < respins); });
    }
    draw(); $("#hsGo").textContent = "RESPIN"; $("#hsNote").textContent = "Coins lock in place · a new coin resets the respins to 3"; openOv("hsov");
    $("#hsGo").onclick = function () {
      if (respins <= 0 || cells.every(Boolean)) { closeOv("hsov"); var full = cells.every(Boolean); var win = total + (full ? jpVal(3) : 0); showPop({ tag: "HOLD & SPIN", title: full ? "GRAND JACKPOT!" : "HOLD & SPIN WIN", amt: win, sub: full ? "All 15 spots filled" : cells.filter(Boolean).length + " coins collected", done: resolve }); return; }
      var fresh = {}, got = false;
      cells.forEach(function (c, i) { if (!c && Math.random() < .16) { cells[i] = val(); fresh[i] = 1; got = true; } });
      respins = got ? 3 : respins - 1; draw(fresh); tone(got ? 880 : 300, .15, "triangle", .05);
      if (respins <= 0 || cells.every(Boolean)) { $("#hsGo").textContent = "COLLECT"; $("#hsNote").textContent = "Bonus over"; }
    };
  });
}
var BW = [["x10", 10, 18], ["x20", 20, 14], ["MINI", "j0", 12], ["x15", 15, 16], ["x50", 50, 6], ["MINOR", "j1", 6], ["x25", 25, 12], ["x100", 100, 3], ["MAJOR", "j2", 2], ["x30", 30, 9], ["x75", 75, 4], ["GRAND", "j3", .6]];
var BW_COL = ["#7b3fe4", "#e91e8c", "#1478c8", "#ff7a1a", "#1f9a2a", "#22c55e", "#c2129b", "#e53950", "#b3127a", "#3d1a9e", "#0b7fa8", "#f5a200"];
function drawWheel(el, labels, colors) {
  var n = labels.length, seg = 360 / n;
  el.style.background = "conic-gradient(" + colors.map(function (c, i) { return c + " " + (i * seg) + "deg " + ((i + 1) * seg) + "deg"; }).join(",") + ")";
  el.innerHTML = labels.map(function (l, i) { return '<span class="lab" style="transform:rotate(' + (i * seg + seg / 2 - 90) + 'deg)">' + l + "</span>"; }).join("") + '<span style="position:absolute;inset:0;border-radius:50%;background:radial-gradient(circle,transparent 58%,rgba(0,0,0,.25))"></span>';
}
function spinWheel(el, idx, n) { var seg = 360 / n, cur = +(el.dataset.rot || 0); var target = cur - (cur % 360) + 360 * 6 + (360 - (idx * seg + seg / 2)); el.dataset.rot = target; el.style.transform = "rotate(" + target + "deg)"; return wait(4300); }
function runBonusWheel() {
  return new Promise(function (resolve) {
    drawWheel($("#bonusWheel"), BW.map(function (s) { return s[0]; }), BW_COL);
    openOv("bwheelov"); var used = false;
    $("#bwHub").onclick = async function () {
      if (used) return; used = true;
      var idx = weighted(BW.map(function (s, i) { return [i, s[2]]; }));
      await spinWheel($("#bonusWheel"), idx, BW.length);
      var s = BW[idx], amt = typeof s[1] === "number" ? s[1] * bet() : jpVal(+s[1][1]);
      closeOv("bwheelov");
      showPop({ tag: "BONUS WHEEL", title: typeof s[1] === "number" ? s[0] + " YOUR BET!" : s[0] + " JACKPOT!", amt: amt, sub: "Bonus Wheel prize", done: resolve });
    };
  });
}
function runPick() {
  return new Promise(function (resolve) {
    var b = bet(), prizes = [2, 3, 4, 5, 8, 10, 15, 20, "MINI"].sort(function () { return Math.random() - .5; }), picks = 3, total = 0;
    $("#chests").innerHTML = prizes.map(function (_, i) { return '<button class="chest" data-ch="' + i + '">' + chestSVG(64, false) + "</button>"; }).join("");
    $("#pickTotal").textContent = "PICK 3 CHESTS"; $("#pickNote").textContent = "3 picks left"; openOv("pickov");
    $$("#chests .chest").forEach(function (el) {
      el.onclick = function () {
        if (picks <= 0 || el.classList.contains("open")) return;
        var p = prizes[+el.dataset.ch], v = typeof p === "number" ? p * b : jpVal(0);
        el.classList.add("open"); el.innerHTML = '<span>' + (typeof p === "number" ? short(v) : "MINI") + "</span>"; total += v; picks--; tone(700, .15, "triangle", .05);
        $("#pickTotal").textContent = fmt(total); $("#pickNote").textContent = picks ? picks + " picks left" : "All picks used";
        if (!picks) {
          $$("#chests .chest:not(.open)").forEach(function (c) { var q = prizes[+c.dataset.ch]; c.classList.add("dead"); c.innerHTML = "<span>" + (typeof q === "number" ? short(q * b) : "MINI") + "</span>"; });
          setTimeout(function () { closeOv("pickov"); showPop({ tag: "PICK BONUS", title: "TREASURE WON!", amt: total, sub: "3 chests opened", done: resolve }); }, 1400);
        }
      };
    });
  });
}

/* ---------------- reward pages ---------------- */
function renderLucky() { drawWheel($("#luckyWheel"), LUCKY.map(short), ["#e91e8c", "#7b3fe4", "#1478c8", "#1f9a2a", "#ff7a1a", "#c2129b", "#0b7fa8", "#f5a200"]); $("#luckySpin").disabled = S.luckyUsed; $("#luckySpin").textContent = S.luckyUsed ? "COME BACK TOMORROW" : "FREE SPIN"; }
async function spinLucky() {
  if (S.luckyUsed) return; S.luckyUsed = true; renderLucky();
  var idx = weighted(LUCKY.map(function (v, i) { return [i, LUCKY_W[i]]; }));
  await spinWheel($("#luckyWheel"), idx, LUCKY.length);
  popup({ tag: "LUCKY WHEEL", title: "YOU WON", amt: LUCKY[idx], sub: "Watch a video to double it" });
}
function renderDaily() {
  $("#dGrid").innerHTML = DAILY.map(function (v, i) {
    var d = i + 1, cls = d < S.dailyDay || (d === S.dailyDay && S.dailyClaimed) ? " done" : d === S.dailyDay ? " today" : "";
    return '<div class="dday' + cls + (d === 7 ? " big" : "") + '"><span>DAY ' + d + "</span>" + (d === 7 ? chestSVG(54, true) : coinSVG(34)) + "<b>" + short(v * 1.25) + "</b>" + (cls === " done" ? '<span class="ok">✓ CLAIMED</span>' : d === S.dailyDay ? '<span class="ok">TODAY</span>' : "") + "</div>";
  }).join("");
  $("#dClaim").textContent = S.dailyClaimed ? "COME BACK TOMORROW" : "CLAIM DAY " + S.dailyDay + " · +" + short(DAILY[S.dailyDay - 1] * 1.25);
  $("#dClaim").disabled = S.dailyClaimed;
}
function renderPiggy() {
  var cap = 2000000 + 500000 * (S.level - 1);
  $("#pigArt").innerHTML = PIG_BIG; $("#pSaved").textContent = fmt(S.piggy);
  $("#pFill").style.width = Math.min(100, S.piggy / cap * 100) + "%";
  $("#pCap").textContent = "Full at " + fmt(cap) + " · grows with your level";
  $("#pBreak").disabled = S.piggy < cap * .2;
}

/* ---------------- tasks, missions, profile ---------------- */
var qTab = "tasks";
function renderQuests() {
  $$("#questov [data-q]").forEach(function (b) { b.classList.toggle("on", b.dataset.q === qTab); });
  $("#qTitle").textContent = qTab === "tasks" ? "DAILY TASKS" : "MISSIONS";
  $("#qSub").textContent = qTab === "tasks" ? "New tasks every day at midnight" : "Long-term goals with bigger rewards";
  var list = qTab === "tasks" ? TASKS : MISSIONS;
  $("#qList").innerHTML = list.map(function (q, i) {
    var key = qTab + i, done = q[2] >= q[1], claimed = S.qClaimed[key], pct = Math.min(100, q[2] / q[1] * 100);
    return '<div class="q' + (done && !claimed ? " ready" : "") + '"><div><b>' + q[0] + '</b><div class="qbar"><span style="width:' + pct + '%"></span></div><small>' + short(Math.min(q[2], q[1])) + " / " + short(q[1]) + " · " + coinSVG(12) + " " + short(q[3]) + "</small></div>" + (claimed ? '<span class="done">✓ DONE</span>' : done ? '<button class="claim" data-claim="' + key + '" data-amt="' + q[3] + '">CLAIM</button>' : '<span class="done" style="color:#fff">' + Math.round(pct) + "%</span>") + "</div>";
  }).join("");
}
function renderProfile() {
  $("#pfName").textContent = S.name; $("#pfLv").textContent = "LEVEL " + S.level + " · PLAYING SINCE NOV 2026";
  $("#avGrid").innerHTML = A.avatars.map(function (a, i) { return '<button class="' + (i === S.avatar ? "on" : "") + '" data-av="' + i + '"><img src="' + a + '" alt=""></button>'; }).join("");
  var st = S.stats;
  $("#pfStats").innerHTML = [["BIGGEST WIN", fmt(st.biggest)], ["TOTAL COINS WON", fmt(st.won)], ["TOTAL SPINS", fmt(st.spins)], ["WIN RATE", Math.round(st.wins / Math.max(1, st.spins) * 100) + "%"], ["BIG WINS", st.big], ["BONUS GAMES", st.bonus]].map(function (s) { return "<div><small>" + s[0] + "</small><b>" + s[1] + "</b></div>"; }).join("");
  renderLevel();
}
function renderPaytable() {
  $("#ptTitle").textContent = byId[T.id].name.toUpperCase();
  $("#ptFeat").textContent = FEAT[T.f].name + " · " + ($("#feat .fnote") ? $("#feat .fnote").textContent : $("#feat small") ? $("#feat small").textContent : "");
  $("#ptSyms").innerHTML = T.sym.map(function (s, i) { var k = kindOf(i), p = PAY[k]; return '<div style="padding:4px;border-radius:10px;background:rgba(0,0,0,.3)"><img src="' + s + '" style="width:44px;height:44px;object-fit:contain" alt=""><div style="font-size:10px;font-weight:900">' + (p ? "5× " + p[5] : k.toUpperCase()) + "</div></div>"; }).join("");
}

/* ---------------- opening things ---------------- */
var back = "lobby";
function open(what) {
  if (what === "store") { back = screen; stab = stab || "offers"; renderStore(); show("store"); }
  else if (what === "wheel") { back = screen; renderLucky(); show("wheelpg"); }
  else if (what === "daily") { back = screen; renderDaily(); show("dailypg"); }
  else if (what === "piggy") { back = screen === "store" ? back : screen; renderPiggy(); show("piggypg"); }
  else if (what === "tasks" || what === "missions") { qTab = what; renderQuests(); openOv("questov"); }
  else if (what === "profile") { renderProfile(); openOv("profileov"); }
  else if (what === "settings") openOv("settings");
  else if (what === "paytable") { renderPaytable(); openOv("paytable"); }
}

/* ---------------- forced outcomes for the tour ---------------- */
function gridWith(fill, cells) { var g = []; for (var r = 0; r < 5; r++) { g.push([]); for (var w = 0; w < 3; w++) g[r].push(fill[(r * 3 + w) % fill.length]); } cells.forEach(function (c) { g[c[0]][c[1]] = c[2]; }); return g; }
var MIX = [0, 5, 2, 7, 1, 6, 3, 4, 2, 0, 6, 1, 5, 3, 7];
function forceScatter(id, n) { var sc = A.themes[id].scatter, cells = []; for (var i = 0; i < n; i++) cells.push([[0, 1, 2, 3, 4][i], [1, 0, 2, 1, 0][i], sc]); return { grid: gridWith(MIX, cells) }; }

/* ---------------- tour ---------------- */
var tourHold = false;
var TOUR = [
  { t: "Loading screen", tag: "Start", d: "A portrait loading screen in the new style: our name in gold, symbols from our machines floating in, a gold loading bar and a rotating tip that teaches a feature.",
    c: ["Portrait, like the rest of the game", "Tips teach the 7 features while players wait", "Uses our own symbols, no new art needed"], run: function () { tourHold = true; runSplash(); } },
  { t: "Lobby", tag: "Lobby", d: "The lobby becomes a real casino floor: a welcome offer with a countdown, a featured machine you can swipe, feature tabs, and a scrolling grid of all 23 machines with HOT, NEW and JACKPOT flags. The bottom bar keeps Tasks and Missions and puts the Lucky Wheel in the middle.",
    c: ["Welcome offer: 10M coins for $4.99, opens the store", "Featured carousel: one machine per feature, slides every 4 s", "Tabs: All, Jackpots, Free Spins, Cascade, Collect, Bonus", "Every tile names its feature; locked tiles show the level or the video", "Dock: Tasks · Daily · Lucky Wheel · Piggy · Missions"], run: function () { show("lobby"); $("#lscroll").scrollTop = 0; } },
  { t: "Unlock a machine", tag: "Lobby", d: "A locked tile opens one clear card instead of a small tag. It shows how far the player is from the level, or offers the video for video machines (one video, as in the game).",
    c: ["2 free machines, then video and level machines (levels 5 to 100)", "Level progress bar on the card", "Video machines also open by themselves at their level"], run: function () { show("lobby"); showUnlock("modern"); } },
  { t: "Coin store", tag: "Store", d: "The store moves to a full page with two tabs. OFFERS holds the welcome offer, the piggy bank and a free-coins video; PACKS holds our six coin packs from 1M to 120M with their bonus percentages.",
    c: ["Offers / Packs tabs", "Our packs: 1M $0.99 up to 120M $39.99", "POPULAR, BEST VALUE and HOT ribbons", "Tap a price: demo purchase, coins fly to the balance"], run: function () { show("lobby"); open("store"); } },
  { t: "Hold & Spin · Pirates", tag: "Feature", d: "Jackpot machines show the four jackpots on the feature board above the cabinet. 4+ scatters start Hold & Spin: coins lock, 3 respins, every new coin resets them, and a full board adds the GRAND.",
    c: ["Board: Mini, Minor, Major, Grand, scaled to the bet", "Lit gold cabinet with our Pirates symbols", "Tap RESPIN to play it out"], run: function () { startGame("pirates"); setTimeout(function () { doSpin(forceScatter("pirates", 4)); }, 300); } },
  { t: "Free Spins x · Christmas Eve", tag: "Feature", d: "3 scatters give 8 free spins. The board turns into a multiplier ladder: every winning free spin climbs one step, x1 → x2 → x3 → x5 → x10. The SPIN button turns blue and counts down.",
    c: ["Ladder lights the current step", "Free spins play automatically", "Total free-spin win at the end (no x2: these are line wins)"], run: function () { startGame("christmas_eve"); setTimeout(function () { doSpin(forceScatter("christmas_eve", 3)); }, 300); } },
  { t: "Cascading Reels · Neon Nights", tag: "Feature", d: "Winning symbols burst, the symbols above fall down and new ones drop in. Each cascade raises the multiplier on the board: WIN x1, then x2, x3 and x5.",
    c: ["Step-by-step: highlight, burst, drop", "CASCADE xN banner on each new win", "Win meter adds up the whole chain"], run: function () { startGame("neon"); setTimeout(function () { doSpin({ grid: gridWith(MIX, [[0, 0, 4], [1, 0, 4], [2, 0, 4], [3, 0, 4], [0, 2, 1], [1, 2, 1], [2, 2, T.wild]]) }); }, 300); } },
  { t: "Diamond Collection · Gems", tag: "Feature", d: "Diamonds land on the reels and fly into the meter on the board. 50 diamonds start 8 Super Free Spins where every win is doubled. Progress is saved per machine.",
    c: ["Diamond badges on the cells", "Meter: 47 / 50 in this demo, so it fills now", "Super Free Spins at x2"], run: function () { startGame("gems"); S.meter.gems = 47; renderFeat(); setTimeout(function () { doSpin({ diamonds: ["1:1", "2:0", "3:2"] }); }, 300); } },
  { t: "Coin Collector · Happy Farm", tag: "Feature", d: "Gold coins with prizes land on reels 1 to 4. When the purple COLLECT badge lands on reel 5, every coin flies to the board and is paid.",
    c: ["Coin prizes from 0.5x to 25x the bet", "Collector only on reel 5", "Board shows the last collect"], run: function () { startGame("farm"); setTimeout(function () { var b = bet(); doSpin({ coins: { "0:2": b * 2, "1:0": b / 2, "2:1": b, "3:2": b * 5 }, collector: "4:1" }); }, 300); } },
  { t: "Bonus Wheel · Modern Suits", tag: "Feature", d: "A jackpot machine with a different bonus: 4+ scatters spin a 12-slice wheel with bet multipliers and all four jackpots.",
    c: ["Tap SPIN in the middle of the wheel", "Slices: x10 to x100, Mini, Minor, Major, Grand", "Prize card with x2 by video"], run: function () { S.demoOpen.modern = true; startGame("modern"); setTimeout(function () { doSpin(forceScatter("modern", 4)); }, 300); } },
  { t: "Pick Bonus · Witch's Cauldron", tag: "Feature", d: "4+ scatters open nine treasure chests. Pick three; the rest are shown afterwards so the player sees what they missed.",
    c: ["Prizes from 2x to 20x the bet, or the Mini", "Unpicked chests revealed at the end", "Total paid with x2 by video"], run: function () { S.demoOpen.witch = true; startGame("witch"); setTimeout(function () { doSpin(forceScatter("witch", 4)); }, 300); } },
  { t: "Big Win", tag: "Gameplay", d: "Big wins take over the screen with light rays, falling coins and a count-up. Three tiers, as in the game: Big (10x the bet), Mega (25x) and Huge (50x).",
    c: ["Count-up to the full amount", "COLLECT flies coins to the balance", "x2 with a rewarded video"], run: function () { startGame("pirates"); setTimeout(function () { var w = A.themes.pirates.wild; doSpin({ grid: [[7, 7, 3], [7, w, 7], [7, 7, 2], [w, 7, 7], [7, 1, 7]] }); }, 300); } },
  { t: "Lucky Wheel", tag: "Rewards", d: "The daily wheel gets its own page, opened from the middle of the dock. Our eight prizes, scaled by level, with the jackpot slice at 1.25M for a level 6 player.",
    c: ["Spin from the hub or the big button", "Prize card with COLLECT or x2", "One free spin per day"], run: function () { show("lobby"); S.luckyUsed = false; open("wheel"); } },
  { t: "Daily Bonus", tag: "Rewards", d: "Our 7-day streak on a full page: claimed days ticked, today glowing, and the day-7 chest twice as wide.",
    c: ["100K on day 1 up to 1M on day 7, +5% per level", "Claim shows the reward card with x2", "Demo link jumps to the next day"], run: function () { show("lobby"); S.dailyClaimed = false; open("daily"); } },
  { t: "Piggy Bank", tag: "Rewards", d: "The piggy bank becomes a page: every paid spin drops 3% of the bet in, the meter shows how full it is, and it can be broken once it is 20% full.",
    c: ["Fills on every spin", "Cap grows with the level", "Break for $2.99 (demo)"], run: function () { show("lobby"); open("piggy"); } },
  { t: "Level up", tag: "Rewards", d: "Every spin fills the level bar next to the avatar. A level-up shows our reward (50,000 x the new level) with x2 by video, and higher bets unlock.",
    c: ["Reward card with coin rain", "Bet ladder opens step by step", "Machines unlock at levels 5, 10, 15, 20, 30…"], run: function () { startGame("pirates"); S.xp = 99; setTimeout(function () { gainXp(); }, 400); } },
  { t: "Tasks & Missions", tag: "Rewards", d: "Our daily tasks and long-term missions in one card with two tabs, each claimed with the x2 video option.",
    c: ["Tasks pay 10K to 30K, missions 30K to 100K", "Progress bars and CLAIM buttons", "Red pips on the dock when something is ready"], run: function () { show("lobby"); open("tasks"); } },
  { t: "Profile", tag: "Profile", d: "Tap the avatar in the top bar for the profile: rename, pick one of our 12 character avatars, and see the player's statistics.",
    c: ["Avatar shows in the top bar of every screen", "Name up to 16 characters", "Biggest win, coins won, spins, win rate, big wins, bonus games"], run: function () { show("lobby"); open("profile"); } }
];
var tourI = 0;
function renderTour() {
  var s = TOUR[tourI];
  $("#caption").innerHTML = '<div class="row"><span class="chip gold">' + (tourI + 1) + " / " + TOUR.length + '</span><span class="chip pink">' + s.tag + "</span></div><h2>" + s.t + "</h2><p>" + s.d + '</p><ul class="changes">' + s.c.map(function (x) { return "<li>" + x + "</li>"; }).join("") + "</ul>";
  $("#steps").innerHTML = TOUR.map(function (x, i) { return '<button class="step" data-step="' + i + '"' + (i === tourI ? ' aria-current="true"' : "") + "><small>" + x.tag + "</small>" + x.t + "</button>"; }).join("");
  $("#prev").disabled = tourI === 0; $("#next").textContent = tourI === TOUR.length - 1 ? "Start again" : "Next screen";
}
function goTour(i) { tourI = (i + TOUR.length) % TOUR.length; closeAll(); clearInterval(splashT); S.auto = 0; S.fs = null; S.spinning = false; renderTour(); TOUR[tourI].run(); }

/* ---------------- events ---------------- */
document.addEventListener("click", function (e) {
  if (!S.sound) S.sound = true;
  var t = e.target.closest("button,[data-open],[data-tile],[data-hd]"); if (!t) return;
  var d = t.dataset;
  if (d.open) { e.preventDefault(); open(d.open); return; }
  if (d.tile) return openTile(d.tile);
  if (d.play) return openTile(d.play);
  if (d.hd != null) return heroGo(+d.hd);
  if (d.cat) { S.cat = d.cat; renderCats(); renderTiles(); return; }
  if (d.st) { stab = d.st; renderStore(); return; }
  if (d.buy) { var n = +d.buy; toast("Purchase complete (demo) · +" + short(n), 1600); fly(t, $("[data-coins]", $("#store")), coinSVG(28), 8, function () { addCoins(n); }); return; }
  if (d.ad) { watchVideo(function () { popup({ tag: "FREE COINS", title: "THANKS FOR WATCHING", amt: +d.ad, x2: false }); }); return; }
  if (d.back !== undefined) { show(back === "store" || back === "wheelpg" || back === "dailypg" || back === "piggypg" ? "lobby" : back); if (screen === "lobby") renderTiles(); return; }
  if (d.close !== undefined) { closeAll(); return; }
  if (d.step) return goTour(+d.step);
  if (d.sw) { t.classList.toggle("on"); if (d.sw === "sound") S.sound = t.classList.contains("on"); return; }
  if (d.q) { qTab = d.q; renderQuests(); return; }
  if (d.claim) { var amt = +d.amt; S.qClaimed[d.claim] = 1; renderQuests(); closeOv("questov"); popup({ tag: qTab === "tasks" ? "DAILY TASK" : "MISSION", title: "COMPLETE!", amt: amt, done: function () { open(qTab); } }); return; }
  if (d.av) { S.avatar = +d.av; renderProfile(); return; }
});
$("#spinBtn").addEventListener("click", function () { if (S.auto > 0 && !S.fs) { S.auto = 0; updSpin(); return; } doSpin(); });
var holdT;
$("#spinBtn").addEventListener("pointerdown", function () { holdT = setTimeout(function () { if (!S.spinning && !S.fs) { S.auto = 10; updSpin(); toast("Auto-spin: 10 spins"); doSpin(); } }, 650); });
["pointerup", "pointerleave"].forEach(function (ev) { $("#spinBtn").addEventListener(ev, function () { clearTimeout(holdT); }); });
$("#autoBtn").addEventListener("click", function () { if (S.fs) return; if (S.auto > 0) { S.auto = 0; updSpin(); return; } S.auto = 10; updSpin(); doSpin(); });
$("#maxBtn").addEventListener("click", function () { if (S.fs || S.spinning) return; S.bi = unlockedSteps() - 1; renderBet(); renderFeat(); });
$("#betDn").addEventListener("click", function () { if (S.bi > 0) { S.bi--; renderBet(); renderFeat(); } });
$("#betUp").addEventListener("click", function () { if (S.bi < unlockedSteps() - 1) { S.bi++; renderBet(); renderFeat(); } });
$("#homeBtn").addEventListener("click", function () { S.auto = 0; S.fs = null; show("lobby"); renderTiles(); });
$("#bwCollect").addEventListener("click", function () { if (bwDone) bwDone(1); });
$("#bwDouble").addEventListener("click", function () { watchVideo(function () { if (bwDone) bwDone(2); }); });
$("#ppOk").addEventListener("click", function () { if (ppDone) ppDone(1); });
$("#ppX2").addEventListener("click", function () { watchVideo(function () { if (ppDone) ppDone(2); }); });
$("#lvOk").addEventListener("click", function () { closeOv("lvup"); addCoins(lvReward); });
$("#lvX2").addEventListener("click", function () { watchVideo(function () { closeOv("lvup"); addCoins(lvReward * 2); }); });
$("#ulkWatch").addEventListener("click", function () { var t = byId[ulkId]; var go = function () { if (t.rule[0] === "v") S.watched[ulkId] = true; else S.demoOpen[ulkId] = true; closeOv("unlock"); renderTiles(); toast(t.name + " unlocked!"); }; if (t.rule[0] === "v") watchVideo(go); else go(); });
$("#luckyHub").addEventListener("click", spinLucky); $("#luckySpin").addEventListener("click", spinLucky);
$("#dClaim").addEventListener("click", function () { if (S.dailyClaimed) return; S.dailyClaimed = true; var v = DAILY[S.dailyDay - 1] * 1.25; renderDaily(); popup({ tag: "DAY " + S.dailyDay + " REWARD", title: "DAILY BONUS", amt: v }); });
$("#dDemo").addEventListener("click", function () { S.dailyDay = S.dailyDay >= 7 ? 1 : S.dailyDay + 1; S.dailyClaimed = false; renderDaily(); });
$("#pBreak").addEventListener("click", function () { var v = S.piggy; S.piggy = 0; renderPiggy(); popup({ tag: "PIGGY BANK", title: "PIGGY BROKEN!", amt: v, x2: false, sub: "Purchase complete (demo)" }); });
$("#pfEdit").addEventListener("click", function () { var i = $("#pfInput"); i.hidden = !i.hidden; if (!i.hidden) { i.value = S.name; i.focus(); } });
$("#pfInput").addEventListener("input", function (e) { S.name = e.target.value.trim() || "Lucky Player"; $("#pfName").textContent = S.name; });
$("#prev").addEventListener("click", function () { goTour(tourI - 1); });
$("#next").addEventListener("click", function () { goTour(tourI + 1); });
$("#resetBtn").addEventListener("click", function () { goTour(0); });

/* ---------------- boot ---------------- */
function fit() {
  var col = $(".phonecol"), avail = Math.min(window.innerHeight - 90, 844), w = Math.min(390, col.parentElement.clientWidth - 24);
  var s = Math.min(1, avail / 844, w / 390); s = Math.max(.5, s);
  $("#phone").style.transform = "scale(" + s + ")"; $("#fit").style.width = 390 * s + "px"; $("#fit").style.height = 844 * s + "px";
}
window.addEventListener("resize", fit);
$$("[data-coinico]").forEach(function (e) { e.outerHTML = coinSVG(30); });
$$("[data-vid]").forEach(function (e) { e.outerHTML = VID; });
$$("[data-ico]").forEach(function (e) { e.innerHTML = ICO[e.dataset.ico]; });
$("[data-chest]").innerHTML = chestSVG(62, true);
$("#splash").style.backgroundImage = "url(" + A.themes.neon.bg + ")";
$("#splashSyms").innerHTML = ["pirates", "christmas_eve", "gems", "farm"].map(function (id) { return '<img src="' + A.themes[id].sym[A.themes[id].wild] + '" alt="">'; }).join("");
drawWheel($("#dockWheel"), ["", "", "", "", "", "", "", ""], ["#e91e8c", "#7b3fe4", "#1478c8", "#1f9a2a", "#ff7a1a", "#c2129b", "#0b7fa8", "#f5a200"]);
$("#worlds").innerHTML = Object.keys(FEAT).map(function (k) { var id = FEAT[k].demo; return '<button class="wt" data-world="' + id + '"><img src="' + byId[id].cover + '" alt="">' + byId[id].name + " <small>· " + FEAT[k].name + "</small></button>"; }).join("");
$("#worlds").addEventListener("click", function (e) { var b = e.target.closest("[data-world]"); if (b) { S.demoOpen[b.dataset.world] = true; closeAll(); startGame(b.dataset.world); } });
renderCats(); renderTiles(); renderHero(); renderLevel(); renderCoins(); fit();
heroT = setInterval(function () { if (screen === "lobby" && !$$(".ov.show").length) heroGo(hi + 1); }, 4000);
setInterval(function () { var d = new Date(), l = 86400 - (d.getHours() * 3600 + d.getMinutes() * 60 + d.getSeconds()); var s = String(Math.floor(l / 3600)).padStart(2, "0") + ":" + String(Math.floor(l / 60) % 60).padStart(2, "0") + ":" + String(l % 60).padStart(2, "0"); $$("[data-timer]").forEach(function (e) { e.textContent = s; }); $("#luckyNext").textContent = "Next free spin in " + s; }, 1000);
renderTour(); runSplash();
})();
