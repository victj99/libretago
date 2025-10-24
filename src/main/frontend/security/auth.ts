import { configureAuth } from '@vaadin/hilla-react-auth'
import { LoggedUserService } from 'Frontend/generated/endpoints'

// Configure auth to use `UserInfoService.getUserInfo`
const auth = configureAuth(LoggedUserService.getUserInfo, {
  getRoles(user) {
    return user.authorities
      .filter(rol => rol !== undefined)
      .map((s) => s.substring(5))
  }
})

// Export auth provider and useAuth hook, which are automatically
// typed to the result of `UserInfoService.getUserInfo`
export const useAuth = auth.useAuth
export const AuthProvider = auth.AuthProvider