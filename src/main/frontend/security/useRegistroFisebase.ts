import { LoggedUserService } from 'Frontend/generated/endpoints'
import { useEffect } from 'react'
import { useAuth } from './auth'
import { getFirebaseToken, messaging } from './firebase-service'
import { onMessage } from 'firebase/messaging'
import { Notification } from '@vaadin/react-components'

// Clave para almacenar el token en localStorage
const FCM_TOKEN_STORAGE_KEY = 'fcm_token_registered'

interface TokenUser {
  token?: string
  usuario?: string
}

/** Hook que se encarga de inicializar FCM y registrar el dispositivo */
export function useRegistroFirebase() {
  const { state, hasAccess } = useAuth()

  const registrarDispositivo = async () => {
    if (!state.user) {
      console.log('Usuario no autenticado')
      return
    }

    const hasRequiredRole = hasAccess({ rolesAllowed: ['PROFESOR', 'APODERADO'] })
    if (!hasRequiredRole) return

    try {
      if ('serviceWorker' in navigator) {
        await navigator.serviceWorker.register('/firebase-messaging-sw.js')
      }

      // Obtener el token del dispositivo
      const token = await getFirebaseToken()
      if (!token) return

      // Verificar si este token ya fue registrado en esta sesión y por quién
      const tokenGuardado = localStorage.getItem(FCM_TOKEN_STORAGE_KEY)

      let tokenUser: TokenUser | null = null
      try {
        tokenUser = tokenGuardado ? JSON.parse(tokenGuardado) : null
      } catch (e) {
        tokenUser = null
      }

      const usuarioActual = state.user?.name || String(state.user)

      if (tokenUser && tokenUser.token === token && tokenUser.usuario === usuarioActual) {
        console.log('Token ya registrado anteriormente por este usuario')
        return
      }

      // Registrar (si token existía para otro usuario, el backend lo reasigna)
      await LoggedUserService.registrarDispositivo(token)
      localStorage.setItem(FCM_TOKEN_STORAGE_KEY, JSON.stringify({ token, usuario: usuarioActual }))

    } catch (error) {
      console.error('Error al registrar dispositivo:', error)
    }
  }

  // Registrar dispositivo automáticamente al autenticarse
  useEffect(() => {
    if (state.user) {
      registrarDispositivo()
    }
  }, [state.user])

  // Cuando se cierre sesión, intentar eliminar el token del servidor y limpiar el localStorage
  useEffect(() => {
    const stored = localStorage.getItem(FCM_TOKEN_STORAGE_KEY)
    if (!state.user && stored) {
      let storedObj = null
      try {
        storedObj = JSON.parse(stored)
      } catch (e) {
        storedObj = null
      }

      const token = storedObj?.token || stored
      if (token) {
        // Eliminamos el token de forma automática
        LoggedUserService.eliminarDispositivo(token).catch((err: any) => console.warn('Error eliminando token en logout', err))
      }

      localStorage.removeItem(FCM_TOKEN_STORAGE_KEY)
    }
  }, [state.user])

  useEffect(() => {
    const hasRequiredRole = hasAccess({ rolesAllowed: ['PROFESOR', 'APODERADO'] })
    if (!hasRequiredRole) return

    onMessage(messaging, payload => {
      Notification.show(payload.notification?.title || '')
    })

  }, [])

  return {
    retryRegistration: registrarDispositivo,
  }
}
