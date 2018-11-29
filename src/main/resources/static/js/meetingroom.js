
function getMrid() {
  return $("#mrid").text();
}

function getUid() {
  return $("#uid").val();
}

var isConnected = false;
var stompClient = null;

/**
 * Connect or disconnect web socket
 */
function connectWebSocket() {
    isConnected = !isConnected;
    if (isConnected) {
        $("#btnConnect").text("Disconnect");
        var socket = new SockJS('/websocket-mr');
        stompClient = Stomp.over(socket);
        stompClient.connect({"uid": getUid()}, function(frame) {
//            console.log('Connected: ' + frame);
            // subscribe 3 topics
            stompClient.subscribe('/topic/mrcontact', function(respnose) {
                displayContacts(JSON.parse(respnose.body));
            });
            stompClient.subscribe('/topic/mrmessage', function(respnose) {
                displayAppendMrMessage(JSON.parse(respnose.body));
            });
            stompClient.subscribe('/topic/alert', function(respnose) {
                displayAlert(JSON.parse(respnose.body));
            });
            // send connect uid to server
            connectWs();
        });
    } else {
        $("#btnConnect").text("Connect");
        if (stompClient != null) {
            // send disconnect uid to server
            disconnectWs();
            // ws disconnect
            stompClient.disconnect();
        }
    }
    console.log("connectWebSocket:" + isConnected);
}

function displayContacts(mrContact) {
//  console.log("displayContacts ...");
  var contactlist = $("#contactlist");
  contactlist.children().remove();
  if (mrContact) {
    mrContact.uids.forEach(function(uid) {
      var msgItem = $("<li/>").addClass("list-group-item")
          .text(uid)
          .appendTo(contactlist);
    });
  }
}

function appendMrMessage(messagelist, msg) {
  var msgItem = $("<li/>").addClass("list-group-item")
      .appendTo(messagelist);
  if (msg.sender == getUid()) {
    msgItem.addClass("list-group-item-success text-right");
    msgItem.text("me:" + msg.message + " at " + msg.sendTime)
  } else if (msg.sender == "sysinfo") {
    msgItem.addClass("list-group-item-info text-center");
    msgItem.text("sys:" + msg.message + " at " + msg.sendTime)
  } else if (msg.sender == "sysdanger") {
    msgItem.addClass("list-group-item-danger text-center");
    msgItem.text("sys:" + msg.message + " at " + msg.sendTime)
  } else {
    msgItem.text(msg.sender + ":" + msg.message + " at " + msg.sendTime);
  }
//  console.log((msg.sender == getUid()) + "-" + (msg.sender == "sysinfo") + "-" + (msg.sender == "sysdanger"));
}

function displayAppendMrMessage(mrMessages) {
  var messagelist = $("#messagelist");
//  console.log(mrMessages);
  if (mrMessages) {
    if (Array.isArray(mrMessages)) {
      mrMessages.forEach(function(msg) {
        appendMrMessage(messagelist, msg);
//        }
      });
    } else {
      appendMrMessage(messagelist, mrMessages);
    }
  }
  // let scroll to last one
  $('ul#messagelist li:last').get(0).scrollIntoView();
//  console.log("-------------");
//  console.log($('ul#messagelist li:last').position().top);
//  $("#messagelistScroll").animate({scrollTop: $('ul#messagelist li:last').position().top - 30}, "slow");
//  messagelist.animate({scrollTop: $('ul#messagelist li:last').position().top - 30}, "slow");
//  $('html, body').animate({scrollTop: $('ul#messagelist li:last').position().top - 30}, "slow");
//  messagelist.scrollTo("*:last", 0);
}

function displayAlert(mrAlert) {
  if (mrAlert) {
    var alertdiv = $("#_alertdiv").clone();
    $("body").prepend(alertdiv);
    alertdiv.attr("id", "alertdiv");
    alertdiv.find(".alert-msg").empty().text(mrAlert.message);
    alertdiv.removeClass("alert-info alert-warning d-none").addClass("alert-" + mrAlert.type).addClass("d-block");
  }
}

function closeAlert(btn) {
  var alertdiv = $(btn).parent("div");
  alertdiv.removeClass("d-block").addClass("d-none");
}

function createMrMessage(message) {
  var mrid = getMrid();
  var uid = getUid();
  var mrMessage = {
    "status" : "",
    "mrid" : mrid,
    "sender" : uid,
    "sendTime" : "",
    "message" : message

  }
  return mrMessage;
}

function connectWs() {
  console.log("connectWs...");
  var mrMessage = createMrMessage();
  stompClient.send("/ws/v1/connect", {}, JSON.stringify(mrMessage));
}

function disconnectWs() {
  console.log("disconnectWs...");
  var mrMessage = createMrMessage();
  stompClient.send("/ws/v1/disconnect", {}, JSON.stringify(mrMessage));
  displayContacts();
}

function sendMrMessage() {
  console.log("sendMrMessage...");
  var uid = getUid();
  var messageBox = $("#messageBox");
  var message = messageBox.val();
  messageBox.empty();
  console.log(uid + ":" + message);
  var mrMessage = createMrMessage(message);
  //如果直接傳到被subscript的topic的話，會收到一個JSON object
  //但如果分成一個送、一個收的話，後端還可以處理message然後再透過@SendTo，
  //這種方式有可能做到把訊息彙總後一次publish多筆message到topic
  stompClient.send("/ws/v1/sendMrMessage", {}, JSON.stringify(mrMessage));
//  stompClient.send("/topic/mrmessage", {}, JSON.stringify(mrMessage));
}


function initButtons() {
  $("#btnConnect").on("click", connectWebSocket);
  $("#btnSend").on("click", sendMrMessage);
  console.log("initButtons...");
}
$( document ).ready(initButtons);