import TokenService from "@/service/TokenService.js";
import { Client } from "@stomp/stompjs";

export const initializeStompClient = () => {
    const token = TokenService.getSessionAccessToken();
    const stompClient = new Client({
      brokerURL: "http://localhost:8080/api/ws",
      debug: (str) => {
        console.log(str);
      },
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      // onConnect: (frame) => {
      //   console.log("Connected: " + frame);
      //   stompClient.value.subscribe(
      //     "/topic/conversations/" + conversationId.value,
      //     (response) => {
      //       messages.value.unshift(JSON.parse(response.body));
      //       totalElements.value++;
      //     }
      //   );
  
      //   if (myInfo.value && myInfo.value.id) {
      //     console.log("my info : " + myInfo.value.id);
      //     stompClient.value.subscribe(
      //       "/topic/notifications/receiver/" + myInfo.value.id,
      //       (response) => {
      //         let responseBody = JSON.parse(response.body);
      //         const Toast = swal.mixin({
      //           toast: true,
      //           position: "bottom-end",
      //           showConfirmButton: false,
      //           timer: 2000,
      //           timerProgressBar: false,
      //           didOpen: (toast) => {
      //             toast.onmouseenter = swal.stopTimer;
      //             toast.onmouseleave = swal.resumeTimer;
      //           },
      //         });
      //         if ("/messages/" + conversationId.value !== responseBody.href) {
      //           // check if recipient is in the conversation
      //           if (
      //             NotificationType.NEW_MESSAGE === responseBody.notificationType
      //           ) {
      //             Toast.fire({
      //               title: `<a href='http://localhost:5173/${responseBody.href}'style='display: inline-block;text-decoration: none; color: #00B0FF; width: 100%; text-align: center;'>Bạn có tin nhắn mới</a>`,
      //             });
      //           } else if (
      //             NotificationType.FRIEND_REQUEST ===
      //             responseBody.notificationType
      //           ) {
      //             Toast.fire({
      //               title: `<a href='http://localhost:5173/${responseBody.href}'style='display: inline-block;text-decoration: none; color: #00B0FF; width: 100%; text-align: center;'>${myInfo.value.fullName} ${responseBody.content}</a>`,
      //             });
      //           }
      //         }
      //       }
      //     );
      //   } else {
      //     console.warn("myInfo is undefined or missing id.");
      //   }
      // },
      onWebSocketError: (error) => {
        console.error("Error with websocket", error);
      },
      onStompError: (frame) => {
        console.error("Broker reported error: " + frame.headers["message"]);
        console.error("Additional details: " + frame.body);
      },
    });
    return stompClient
}