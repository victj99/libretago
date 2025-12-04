import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { Button, Icon, Notification, PasswordField, Select } from '@vaadin/react-components'
import Alumno2DTO from 'Frontend/generated/com/utp/libretago/classes/dto/Alumno2DTO'
import UsuarioInstitucionDTO from 'Frontend/generated/com/utp/libretago/classes/dto/UsuarioInstitucionDTO'
import { LoggedUserService } from 'Frontend/generated/endpoints'
import { useAuth } from 'Frontend/security/auth'
import { useEffect, useState } from 'react'
import { FaUser } from "react-icons/fa"

export const config: ViewConfig = {
  menu: {
    title: 'Mi Perfil',
    order: 99,
  },
  loginRequired: true
}

export default function PerfilView() {
  const { state, hasAccess } = useAuth()
  const [instituciones, setInstituciones] = useState<UsuarioInstitucionDTO[]>([])
  const [currentId, setCurrentId] = useState<string>('')

  // Estado para cambio de contraseña
  const [contrasenaActual, setContrasenaActual] = useState('')
  const [contrasenaNueva, setContrasenaNueva] = useState('')
  const [contrasenaConfirmar, setContrasenaConfirmar] = useState('')
  const [cambiandoContrasena, setCambiandoContrasena] = useState(false)

  // Estado para alumnos del apoderado
  const [alumnos, setAlumnos] = useState<Alumno2DTO[]>([])
  const isApoderado = hasAccess({ rolesAllowed: ['APODERADO'] })

  useEffect(() => {
    if (hasAccess({ rolesAllowed: ['PROFESOR'] })) {
      cargarContexto()
    }
    if (isApoderado) {
      cargarAlumnos()
    }
  }, [])

  async function cargarContexto() {
    try {
      const [lista, actual] = await Promise.all([
        LoggedUserService.listarInstituciones(),
        LoggedUserService.obtenerIdInstitucionActual()
      ])
      setInstituciones(lista)
      if (actual) setCurrentId(actual.toString())
      else if (lista.length > 0) setCurrentId(lista[0].institucionEducativaId?.toString() || '')
    } catch (e) {
      console.error("Error cargando contexto", e)
    }
  }

  async function cargarAlumnos() {
    try {
      const lista = await LoggedUserService.listarAlumnosApoderado()
      setAlumnos(lista)
    } catch (e) {
      console.error("Error cargando alumnos", e)
    }
  }

  async function cambiarContexto(nuevoId: string) {
    if (!nuevoId || nuevoId === currentId) return
    try {
      await LoggedUserService.cambiarInstitucion(parseInt(nuevoId))
      window.location.reload()
    } catch (e) {
      console.error("Error cambiando contexto", e)
    }
  }

  async function handleCambiarContrasena() {
    // Validaciones
    if (!contrasenaActual || !contrasenaNueva || !contrasenaConfirmar) {
      Notification.show('Por favor complete todos los campos', { theme: 'error', position: 'top-center' })
      return
    }

    if (contrasenaNueva.length < 6) {
      Notification.show('La nueva contraseña debe tener al menos 6 caracteres', { theme: 'error', position: 'top-center' })
      return
    }

    if (contrasenaNueva !== contrasenaConfirmar) {
      Notification.show('Las contraseñas nuevas no coinciden', { theme: 'error', position: 'top-center' })
      return
    }

    setCambiandoContrasena(true)
    try {
      await LoggedUserService.cambiarContrasena(contrasenaActual, contrasenaNueva)
      Notification.show('Contraseña actualizada correctamente', { theme: 'success', position: 'top-center' })
      // Limpiar campos
      setContrasenaActual('')
      setContrasenaNueva('')
      setContrasenaConfirmar('')
    } catch (e: any) {
      const mensaje = e?.message || 'Error al cambiar la contraseña'
      Notification.show(mensaje, { theme: 'error', position: 'top-center' })
    } finally {
      setCambiandoContrasena(false)
    }
  }

  return (
    <div className="flex flex-col items-center p-m gap-m">
      <div className="bg-base p-l rounded-l shadow-s w-full max-w-md flex flex-col gap-m items-center">
        <div className="bg-primary-10 p-l rounded-full">
          <FaUser className="icon-xl text-primary" style={{ width: '64px', height: '64px' }} />
        </div>

        <div className="text-center">
          <h2 className="text-xl font-bold">{state.user?.name}</h2>
          <p className="text-secondary">
            {state.user?.authorities.map(role => role?.replace('ROLE_', '')).join(', ')}
          </p>
        </div>

        {/* Sección de alumnos para apoderados */}
        {isApoderado && alumnos.length > 0 && (
          <div className="w-full pt-m border-t border-contrast-10">
            <h3 className="text-m font-semibold mb-s">Mis Alumnos</h3>
            <div className="flex flex-col gap-s">
              {alumnos.map((alumno) => (
                <div
                  key={alumno.id}
                  className="flex items-center gap-m p-s bg-contrast-5 rounded-m"
                >
                  <div className="bg-primary-10 p-s rounded-full">
                    <Icon icon="vaadin:academy-cap" className="text-primary" style={{ width: '24px', height: '24px' }} />
                  </div>
                  <div className="flex-1">
                    <p className="font-medium">{alumno.nombres} {alumno.apellidos}</p>
                    {alumno.codigoAlumno && (
                      <p className="text-secondary text-s">Código: {alumno.codigoAlumno}</p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {hasAccess({ rolesAllowed: ['PROFESOR'] }) && instituciones.length > 1 && (
          <div className="w-full pt-m border-t border-contrast-10">
            <h3 className="text-m font-semibold mb-s">Cambiar Colegio</h3>
            <Select
              label="Selecciona una institución"
              items={instituciones.map(i => ({ label: i.nombreInstitucion, value: i.institucionEducativaId?.toString() }))}
              value={currentId}
              onChange={(e) => cambiarContexto(e.target.value)}
              className="w-full"
            />
          </div>
        )}

        {/* Sección de cambio de contraseña */}
        <div className="w-full pt-m border-t border-contrast-10">
          <h3 className="text-m font-semibold mb-s">Cambiar Contraseña</h3>

          <div className="flex flex-col gap-s">
            <PasswordField
              label="Contraseña actual"
              value={contrasenaActual}
              onValueChanged={(e) => setContrasenaActual(e.detail.value)}
              className="w-full"
            />

            <PasswordField
              label="Nueva contraseña"
              value={contrasenaNueva}
              onValueChanged={(e) => setContrasenaNueva(e.detail.value)}
              helperText="Mínimo 6 caracteres"
              className="w-full"
            />

            <PasswordField
              label="Confirmar nueva contraseña"
              value={contrasenaConfirmar}
              onValueChanged={(e) => setContrasenaConfirmar(e.detail.value)}
              className="w-full"
            />

            <Button
              theme="primary"
              onClick={handleCambiarContrasena}
              disabled={cambiandoContrasena}
              className="mt-s"
            >
              {cambiandoContrasena ? 'Guardando...' : 'Cambiar contraseña'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
