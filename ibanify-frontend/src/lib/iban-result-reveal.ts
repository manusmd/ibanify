import { getIbanStructureSegments } from '@/lib/iban-structure'
import type { ValidateIbanResponse } from '@/types/iban-api'

export const REVEAL_STAGGER_MS = 52
export const REVEAL_TICK_MS = 42
export const REVEAL_TICK_MS_IBAN_INPUT = 22
export const REVEAL_IBAN_CHARS_PER_TICK = 3
const REVEAL_TAIL_MS = 240

export type ResultRevealDelays = {
  segments: number[]
  bankName: number | null
  bankZip: number | null
  bankCity: number | null
  bankCode: number | null
  bic: number | null
}

export function buildResultRevealDelays(
  result: ValidateIbanResponse,
): ResultRevealDelays {
  let d = 0
  const step = REVEAL_STAGGER_MS
  const segments = getIbanStructureSegments(result.normalizedIban).map(() => {
    const x = d
    d += step
    return x
  })
  const bankName =
    result.bankName != null && result.bankName !== ''
      ? (() => {
          const x = d
          d += step
          return x
        })()
      : null
  const bankZip =
    result.bankZip != null && result.bankZip !== ''
      ? (() => {
          const x = d
          d += step
          return x
        })()
      : null
  const bankCity =
    result.bankCity != null && result.bankCity !== ''
      ? (() => {
          const x = d
          d += step
          return x
        })()
      : null
  const bankCode =
    result.bankCode != null && result.bankCode !== ''
      ? (() => {
          const x = d
          d += step
          return x
        })()
      : null
  const bic =
    result.bic != null && result.bic !== ''
      ? (() => {
          const x = d
          d += step
          return x
        })()
      : null
  return {
    segments,
    bankName,
    bankZip,
    bankCity,
    bankCode,
    bic,
  }
}

export function computeRevealDurationMs(result: ValidateIbanResponse): number {
  const delays = buildResultRevealDelays(result)
  const ibanSteps = Math.ceil(
    result.displayIban.length / REVEAL_IBAN_CHARS_PER_TICK,
  )
  let maxEnd = ibanSteps * REVEAL_TICK_MS_IBAN_INPUT + 120

  const segs = getIbanStructureSegments(result.normalizedIban)
  segs.forEach((s, i) => {
    const start = delays.segments[i] ?? 0
    maxEnd = Math.max(maxEnd, start + s.value.length * REVEAL_TICK_MS + REVEAL_STAGGER_MS)
  })
  const bankEnd = (text: string, start: number | null) => {
    if (start == null) return
    maxEnd = Math.max(maxEnd, start + text.length * REVEAL_TICK_MS + REVEAL_STAGGER_MS)
  }
  if (result.bankName) bankEnd(result.bankName, delays.bankName)
  if (result.bankZip) bankEnd(result.bankZip, delays.bankZip)
  if (result.bankCity) bankEnd(result.bankCity, delays.bankCity)
  if (result.bankCode) bankEnd(result.bankCode, delays.bankCode)
  if (result.bic) bankEnd(result.bic, delays.bic)

  return maxEnd + REVEAL_TAIL_MS
}
