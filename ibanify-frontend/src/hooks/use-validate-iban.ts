import { useCallback, useEffect, useLayoutEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { computeRevealDurationMs } from '@/lib/iban-result-reveal'
import { userFacingValidateIbanError } from '@/lib/iban-validate-user-message'
import { ValidateIbanClientError, validateIban } from '@/lib/validate-iban'
import type { ValidateIbanResponse } from '@/types/iban-api'

export type ValidateIbanPhase =
  | 'idle'
  | 'loading'
  | 'revealing'
  | 'success'
  | 'error'

export function useValidateIban(options?: {
  onRevealComplete?: (displayIban: string) => void
}) {
  const { t, i18n } = useTranslation()
  const [phase, setPhase] = useState<ValidateIbanPhase>('idle')
  const [result, setResult] = useState<ValidateIbanResponse | null>(null)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const onRevealCompleteRef = useRef(options?.onRevealComplete)
  useLayoutEffect(() => {
    onRevealCompleteRef.current = options?.onRevealComplete
  }, [options?.onRevealComplete])

  const validate = useCallback(
    async (input: string): Promise<ValidateIbanResponse | null> => {
      setPhase('loading')
      setResult(null)
      setErrorMessage(null)
      try {
        const data = await validateIban(input, {
          acceptLanguage: i18n.language,
        })
        setResult(data)
        setPhase('revealing')
        return data
      } catch (err) {
        setPhase('error')
        if (err instanceof ValidateIbanClientError) {
          setErrorMessage(userFacingValidateIbanError(err, t))
        } else {
          setErrorMessage(t('errors.generic'))
        }
        return null
      }
    },
    [i18n.language, t],
  )

  useEffect(() => {
    if (phase !== 'revealing' || !result) {
      return
    }
    const ms = computeRevealDurationMs(result)
    const id = window.setTimeout(() => {
      onRevealCompleteRef.current?.(result.displayIban)
      setPhase('success')
    }, ms)
    return () => window.clearTimeout(id)
  }, [phase, result])

  const reset = useCallback(() => {
    setPhase('idle')
    setResult(null)
    setErrorMessage(null)
  }, [])

  return {
    phase,
    result,
    errorMessage,
    validate,
    reset,
  }
}
