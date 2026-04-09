import { useEffect, useState } from 'react'
import { randomCharMatching } from '@/lib/iban-scramble'

export function useScrambleReveal(
  target: string,
  active: boolean,
  startDelayMs: number,
  tickMs = 72,
  charsPerTick = 1,
): string {
  const [display, setDisplay] = useState('')

  const skipAnimation = !active || target.length === 0

  useEffect(() => {
    if (skipAnimation) {
      return
    }

    let cancelled = false
    let lockCount = 0
    let intervalId: ReturnType<typeof window.setInterval> | undefined
    let afterScrambleDelayId: ReturnType<typeof window.setTimeout> | undefined

    const step = Math.max(1, charsPerTick)

    const initScramble = target
      .split('')
      .map((_, i) => randomCharMatching(target, i))
      .join('')

    const tick = () => {
      if (cancelled) return
      lockCount = Math.min(lockCount + step, target.length)
      const prefix = target.slice(0, lockCount)
      const rest = target
        .slice(lockCount)
        .split('')
        .map((_, idx) => randomCharMatching(target, lockCount + idx))
        .join('')
      setDisplay(prefix + rest)
      if (lockCount >= target.length && intervalId !== undefined) {
        window.clearInterval(intervalId)
        intervalId = undefined
        setDisplay(target)
      }
    }

    const bootId = window.setTimeout(() => {
      if (cancelled) return
      setDisplay(initScramble)
      afterScrambleDelayId = window.setTimeout(() => {
        if (cancelled) return
        intervalId = window.setInterval(tick, tickMs)
      }, startDelayMs)
    }, 0)

    return () => {
      cancelled = true
      window.clearTimeout(bootId)
      if (afterScrambleDelayId !== undefined) {
        window.clearTimeout(afterScrambleDelayId)
      }
      if (intervalId !== undefined) {
        window.clearInterval(intervalId)
      }
    }
  }, [skipAnimation, target, startDelayMs, tickMs, charsPerTick])

  if (!active) {
    return ''
  }
  if (target.length === 0) {
    return target
  }
  return display
}
