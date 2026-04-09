import { useScrambleReveal } from '@/hooks/use-scramble-reveal'
import { REVEAL_TICK_MS } from '@/lib/iban-result-reveal'

export function ScrambleSpan({
  text,
  reveal,
  startDelayMs,
  tickMs = REVEAL_TICK_MS,
  charsPerTick = 1,
  className,
}: {
  text: string
  reveal: boolean
  startDelayMs: number
  tickMs?: number
  charsPerTick?: number
  className?: string
}) {
  const shown = useScrambleReveal(
    text,
    reveal,
    startDelayMs,
    tickMs,
    charsPerTick,
  )
  return <span className={className}>{reveal ? shown : text}</span>
}
