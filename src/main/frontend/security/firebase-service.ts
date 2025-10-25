// FCM Service - Maneja la configuración de Firebase Cloud Messaging
import { initializeApp } from 'firebase/app'
import { getMessaging, getToken } from 'firebase/messaging'

const firebaseConfig = {
  apiKey: "AIzaSyCVJi_2Lb7FgZKD71rZ0uSnOqoJ7QxJae4",
  projectId: "libretago",
  messagingSenderId: "72686370003",
  appId: "1:72686370003:web:53bb47345cb7147d6aa826",
}

const app = initializeApp(firebaseConfig)
export const messaging = getMessaging(app)

/** Obtiene el token FCM del dispositivo */
export async function getFirebaseToken(): Promise<string | undefined> {
  try {
    // Solicitar permiso de notificaciones
    const permission = await Notification.requestPermission()
    if (permission !== 'granted') {
      console.warn('Permiso de notificaciones denegado')
      return
    }

    return await getToken(messaging, {
      vapidKey: 'BO-T4Dta3Yp1QbYMAamwa5WTKGbHDWF92fR6Ekwo9p-9qCJjM_Cv3cwGOpgCsudZPA78cRMhCAzHYpUwBHXGhN8',
    })
  } catch (error) {
    console.error('Error al obtener token FCM:', error)
    return
  }
}
