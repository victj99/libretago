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
    <main className="flex justify-center items-center w-full h-full">
      <LoginForm
        action="login"
        error={hasError}
        noForgotPassword
        i18n={{
          form: {
            title: 'Iniciar sesión',
            username: 'Nombre de usuario',
            password: 'Contraseña',
            submit: 'Iniciar',
            forgotPassword: '',
          },
          errorMessage: {
            title: 'Usuario o contraseña incorrectos',
            message: 'Verifica que el nombre de usuario y la contraseña sean correctos e inténtalo de nuevo.',
            username: 'Se requiere nombre de usuario',
            password: 'Se requiere contraseña',
          }
        }} />
    </main>
  )
}