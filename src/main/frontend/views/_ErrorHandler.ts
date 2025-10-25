import { EndpointError } from '@vaadin/hilla-frontend'
import { Notification } from '@vaadin/react-components/Notification'

export default function handleError(error: any) {
  console.error('An unexpected error occurred', error)

  let message = 'An unexpected error occurred. Please try again later.'

  if (error instanceof EndpointError) {
    message = error.message
  }

  Notification.show(message, { position: 'bottom-end', theme: 'error' })
}
