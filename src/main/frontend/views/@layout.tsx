import { createMenuItems } from '@vaadin/hilla-file-router/runtime.js'
import { ViewConfig } from '@vaadin/hilla-file-router/types.js'
import { AppLayout, AppLayoutElement, Icon, ProgressBar, Scroller, SideNav, SideNavItem } from '@vaadin/react-components'
import { useAuth } from 'Frontend/security/auth'
import { useRegistroFirebase } from 'Frontend/security/useRegistroFisebase'
import { Suspense, useEffect, useRef } from 'react'
import { AiFillNotification } from "react-icons/ai"
import { FaSchoolFlag, FaUserGraduate, FaUsers } from "react-icons/fa6"
import { IconType } from 'react-icons/lib'
import { RiContactsBook3Fill } from "react-icons/ri"
import { Link, Outlet, useLocation, useNavigate } from 'react-router'

const IconosPaginas: Record<string, IconType> = {
  "/alumnos": FaUserGraduate,
  "/instituciones-educativas": FaSchoolFlag,
  "/usuarios-colegio": FaUsers,
  "/usuarios-profesor": FaUsers,
  "/grupos": RiContactsBook3Fill,
  "/administrar-notificaciones": AiFillNotification,
  "/notificaciones": AiFillNotification,
}

function Header() {
  // TODO Replace with real application logo and name
  return (
    <div className="flex p-m gap-m items-center" slot="drawer">
      <Icon icon="vaadin:cubes" className="text-primary icon-l" />
      <span className="font-semibold text-l">Libretago</span>
    </div>
  )
}

function MainMenu() {
  const navigate = useNavigate()
  const location = useLocation()
  const { logout, state } = useAuth()

  return (
    <SideNav
      className="mx-m"
      location={location}
      onNavigate={({ path }) => path != null && navigate(path)}
    >
      {createMenuItems().map(({ to, icon, title }) => {
        const Icono = IconosPaginas[to]

        return <SideNavItem path={to} key={to}>
          {icon && <Icon icon={icon} slot="prefix" />}

          {/* @ts-ignore */}
          {Icono && <Icono slot='prefix' />}
          {title}
        </SideNavItem>
      })}

      <br />
      <SideNavItem className='item-error' onClick={logout}>
        <Icon icon='vaadin:exit' slot="prefix" />
        Cerrar sesión
      </SideNavItem>
    </SideNav>
  )
}

export const config: ViewConfig = {
  loginRequired: true
}

export default function MainLayout() {
  const appLayoutRef = useRef<AppLayoutElement>(null)

  useRegistroFirebase()

  useEffect(() => {
    const appLayout = appLayoutRef.current
    if (appLayout) {
      appLayout.style.setProperty('--vaadin-app-layout-touch-optimized', 'true');
      (appLayout as any)._updateTouchOptimizedMode()
    }
  }, [appLayoutRef.current])


  return (
    <AppLayout ref={appLayoutRef} primarySection="drawer">

      <Header />
      <Scroller slot="drawer">
        <MainMenu />
      </Scroller>
      <Suspense fallback={<ProgressBar indeterminate={true} className="m-0" />}>
        <Outlet />
      </Suspense>

      <div
        slot="navbar touch-optimized"
        className="flex w-full justify-evenly self-stretch"
      >
        {createMenuItems().map(({ to, icon, title }) => {
          const Icono = IconosPaginas[to]
          return <Link className='flex flex-col justify-center items-center' to={to} key={to}>
            {icon && <Icon icon={icon} />}
            {/* @ts-ignore */}
            {Icono && <Icono size={22} />}
            <span>{title}</span>
          </Link>
        })}
      </div>
    </AppLayout>
  )
}
