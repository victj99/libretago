import { CgSpinner } from "react-icons/cg"

interface Props {
  mostrar: boolean
  zIndex?: number
  className?: string
  size?: number | string
}

export default function LoadingOverlay({ mostrar, zIndex = 1, className, size = 70 }: Props) {
  if (!mostrar) return <></>

  return <div
    style={{ zIndex: zIndex }}
    className={`${className} loading-overlay`}
  >
    <CgSpinner size={size} className="text-primary animate-spin" />
  </div>
}