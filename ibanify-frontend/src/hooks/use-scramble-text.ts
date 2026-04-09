import { useEffect, useState } from 'react'
import { randomIbanLikeString } from '@/lib/iban-scramble'

export function useScrambleText(
  active: boolean,
  length: number,
  tickMs = 55,
): string {
  const [text, setText] = useState('')

  useEffect(() => {
    if (!active || length < 1) {
      return
    }
    const update = () => {
      setText(randomIbanLikeString(length))
    }
    const t0 = window.setTimeout(update, 0)
    const id = window.setInterval(update, tickMs)
    return () => {
      window.clearTimeout(t0)
      window.clearInterval(id)
    }
  }, [active, length, tickMs])

  if (!active || length < 1) {
    return ''
  }
  return text
}
