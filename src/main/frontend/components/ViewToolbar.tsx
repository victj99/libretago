import { DrawerToggle } from '@vaadin/react-components'
import { PropsWithChildren } from 'react'

export function Group(props: PropsWithChildren) {
  return <div className="flex flex-col items-stretch gap-s md:flex-row md:items-center">{props.children}</div>
}

export type ViewToolbarProps = {
  title: string
} & PropsWithChildren

export function ViewToolbar(props: ViewToolbarProps) {
  return (
    <header className="flex flex-col justify-between items-stretch gap-m flex-row md:items-center">
      <div className="flex items-center">
        <DrawerToggle className="m-0 max-[801px]:hidden" />
        <h1 className="text-xl m-0 font-light">{props.title}</h1>
      </div>
      {props.children && (
        <div className="flex grow gap-s">{props.children}</div>
      )}
    </header>
  )
}
