importScripts('https://www.gstatic.com/firebasejs/12.4.0/firebase-app-compat.js')
importScripts('https://www.gstatic.com/firebasejs/12.4.0/firebase-messaging-compat.js')

const firebaseConfig = {
  apiKey: "AIzaSyCVJi_2Lb7FgZKD71rZ0uSnOqoJ7QxJae4",
  projectId: "libretago",
  messagingSenderId: "72686370003",
  appId: "1:72686370003:web:53bb47345cb7147d6aa826",
}

const app = firebase.initializeApp(firebaseConfig)
const messaging = firebase.messaging(app)

messaging.onBackgroundMessage((payload) => {
  const notificationTitle = payload.notification?.title || 'Notificación'
  const notificationOptions = {
    body: payload.notification?.body || '',
    icon: payload.notification?.icon || '/icon-192x192.png',
    badge: '/badge-72x72.png',
    data: payload.data || {},
    tag: 'libretago-notification', // Agrupa notificaciones del mismo tipo
    requireInteraction: false, // Usuario puede descartar automáticamente
  }

  self.registration.showNotification(notificationTitle, notificationOptions)
})