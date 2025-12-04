import { LoginForm } from "@vaadin/react-components"
import { ViewConfig } from "@vaadin/hilla-file-router/types.js"
import { useSearchParams } from "react-router"

export const config: ViewConfig = {
  skipLayouts: true,
  menu: { exclude: true }
}

export default function LoginView() {
  const [searchParams] = useSearchParams()
  const hasError = searchParams.has("error")

  return (
    <main className="relative flex justify-center items-center w-full min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 overflow-hidden">
      {/* Contenedor del formulario */}
      <div className="relative z-10 w-full max-w-md mx-4 sm:mx-6 md:mx-8">
        <div className="bg-white/80 backdrop-blur-xl rounded-3xl shadow-2xl p-8 sm:p-10 md:p-12 border border-white/20">
          {/* Logo o título */}
          <div className="text-center mb-8">
            <img src="/images/libretago_icon.png" width={80} alt="" />
            <h1 className="text-3xl sm:text-4xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent mb-2">
              Libretago
            </h1>
            <p className="text-gray-600 text-sm sm:text-base">Bienvenido de nuevo</p>
          </div>

          {/* Formulario de login */}
          <LoginForm
            action="login"
            error={hasError}
            noForgotPassword
            i18n={{
              form: {
                title: 'Iniciar sesión',
                username: 'Nombre de usuario',
                password: 'Contraseña',
                submit: 'Iniciar sesión',
                forgotPassword: '',
              },
              errorMessage: {
                title: 'Usuario o contraseña incorrectos',
                message: 'Verifica que el nombre de usuario y la contraseña sean correctos e inténtalo de nuevo.',
                username: 'Se requiere nombre de usuario',
                password: 'Se requiere contraseña',
              }
            }} />
        </div>
      </div>
    </main>
  )
}