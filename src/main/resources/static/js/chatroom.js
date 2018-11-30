
function getCrid() {
  return $("#crid").text();
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
        var socket = new SockJS('/websocket-cr');
        stompClient = Stomp.over(socket);
        stompClient.connect({"uid": getUid()}, function(frame) {
//            console.log('Connected: ' + frame);
            // subscribe 3 topics
            stompClient.subscribe('/topic/crcontact', function(respnose) {
                displayContacts(JSON.parse(respnose.body));
            });
            stompClient.subscribe('/topic/crmessage', function(respnose) {
                displayAppendCrMessage(JSON.parse(respnose.body));
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

function displayContacts(crContact) {
//  console.log("displayContacts ...");
  var contactlist = $("#contactlist");
  contactlist.children().remove();
  if (crContact) {
    crContact.uids.forEach(function(uid) {
      var msgItem = $("<li/>").addClass("list-group-item")
          .text(uid)
          .appendTo(contactlist);
    });
  }
}

function appendCrMessage(messagelist, msg) {
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

function displayAppendCrMessage(crMessages) {
  var messagelist = $("#messagelist");
  if (crMessages) {
    if (Array.isArray(crMessages)) {
      crMessages.forEach(function(msg) {
        appendCrMessage(messagelist, msg);
      });
    } else {
      appendCrMessage(messagelist, crMessages);
    }
  }
  // let scroll to last one
  $('ul#messagelist li:last').get(0).scrollIntoView();
}

function displayAlert(crAlert) {
  if (crAlert) {
    var alertdiv = $("#_alertdiv").clone();
    $("body").prepend(alertdiv);
    alertdiv.attr("id", "alertdiv");
    alertdiv.find(".alert-msg").empty().text(crAlert.message);
    alertdiv.removeClass("alert-info alert-warning d-none").addClass("alert-" + crAlert.type).addClass("d-block");
  }
}

function closeAlert(btn) {
  var alertdiv = $(btn).parent("div");
  alertdiv.removeClass("d-block").addClass("d-none");
}

function createCrMessage(message) {
  var crid = getCrid();
  var uid = getUid();
  var crMessage = {
    "status" : "",
    "crid" : crid,
    "sender" : uid,
    "sendTime" : "",
    "message" : message

  }
  return crMessage;
}

function connectWs() {
  console.log("connectWs...");
  var crMessage = createCrMessage();
  stompClient.send("/ws/v1/connect", {}, JSON.stringify(crMessage));
}

function disconnectWs() {
  console.log("disconnectWs...");
  var crMessage = createCrMessage();
  stompClient.send("/ws/v1/disconnect", {}, JSON.stringify(crMessage));
  displayContacts();
}

function sendCrMessage() {
//  console.log("sendCrMessage...");
  var uid = getUid();
  var messageBox = $("#messageBox");
  var message = messageBox.val();
  messageBox.empty();
  console.log(uid + ":" + message);
  var crMessage = createCrMessage(message);
  //如果直接傳到被subscript的topic的話，會收到一個JSON object
  //但如果分成一個送、一個收的話，後端還可以處理message然後再透過@SendTo，
  //這種方式有可能做到把訊息彙總後一次publish多筆message到topic
  stompClient.send("/ws/v1/sendCrMessage", {}, JSON.stringify(crMessage));
//  stompClient.send("/topic/crmessage", {}, JSON.stringify(crMessage));
}


function initButtons() {
  $("#btnConnect").on("click", connectWebSocket);
  $("#btnSend").on("click", sendCrMessage);
  console.log("initButtons...");
}
$( document ).ready(initButtons);