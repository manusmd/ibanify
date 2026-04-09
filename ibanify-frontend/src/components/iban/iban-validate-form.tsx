import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { AnimatePresence, motion } from 'motion/react'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Skeleton } from '@/components/ui/skeleton'
import { IbanValidateResultDetails } from '@/components/iban/iban-validate-result-details'
import { useScrambleReveal } from '@/hooks/use-scramble-reveal'
import { useScrambleText } from '@/hooks/use-scramble-text'
import { useValidateIban } from '@/hooks/use-validate-iban'
import { EXAMPLE_IBANS, formatIbanForInput } from '@/lib/example-ibans'
import {
  REVEAL_IBAN_CHARS_PER_TICK,
  REVEAL_TICK_MS_IBAN_INPUT,
} from '@/lib/iban-result-reveal'
import { stripIbanToAlnum } from '@/lib/iban-scramble'

export function IbanValidateForm() {
  const { t } = useTranslation()
  const [ibanInput, setIbanInput] = useState('')
  const [scrambleLength, setScrambleLength] = useState(0)
  const { phase, result, errorMessage, validate, reset } = useValidateIban({
    onRevealComplete: setIbanInput,
  })

  const busyVisual = phase === 'loading' || phase === 'revealing'
  const inputObscured = phase === 'loading'
  const inputRandomScramble = useScrambleText(
    phase === 'loading',
    scrambleLength,
  )
  const inputReveal = useScrambleReveal(
    result?.displayIban ?? '',
    phase === 'revealing',
    0,
    REVEAL_TICK_MS_IBAN_INPUT,
    REVEAL_IBAN_CHARS_PER_TICK,
  )
  const inputDisplay =
    phase === 'loading'
      ? inputRandomScramble
      : phase === 'revealing'
        ? inputReveal
        : ibanInput

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    const alnumLen = stripIbanToAlnum(ibanInput).length
    setScrambleLength(Math.max(12, alnumLen))
    const data = await validate(ibanInput)
    if (data) {
      setScrambleLength(Math.max(12, data.normalizedIban.length))
    }
  }

  const inputInvalid = phase === 'error' && errorMessage != null
  const showResultPanel =
    result != null && (phase === 'revealing' || phase === 'success')

  return (
    <div className="flex w-full flex-col gap-4">
      <form onSubmit={onSubmit} className="flex flex-col gap-3 text-left">
        <div className="space-y-1">
          <Label htmlFor="iban-input" className="text-xs font-medium">
            {t('form.ibanLabel')}
          </Label>
          <Input
            id="iban-input"
            autoComplete="off"
            spellCheck={false}
            placeholder={t('form.placeholder')}
            value={inputDisplay}
            onChange={(e) => {
              if (!busyVisual) {
                setIbanInput(e.target.value)
              }
            }}
            disabled={busyVisual}
            aria-busy={busyVisual}
            aria-invalid={inputInvalid}
            aria-describedby="iban-hint result-region"
            className={`h-10 rounded-lg border-border/80 bg-background/50 font-mono text-sm tracking-wide transition-[filter,opacity] duration-200 ${
              inputObscured
                ? 'text-muted-foreground blur-[1.5px] [letter-spacing:0.2em]'
                : 'text-foreground tracking-wide'
            }`}
          />
          <p
            id="iban-hint"
            className="text-muted-foreground text-xs leading-snug"
          >
            {t('form.hint')}
          </p>
          <div className="pt-1">
            <p className="text-muted-foreground mb-1.5 text-[10px] font-medium uppercase tracking-wider">
              {t('form.examplesTitle')}
            </p>
            <div className="flex flex-wrap gap-1.5">
              {EXAMPLE_IBANS.map((ex) => (
                <Button
                  key={ex.iban}
                  type="button"
                  variant="outline"
                  size="sm"
                  disabled={busyVisual}
                  className="h-auto max-w-full rounded-md border-border/70 px-2 py-1.5 text-left text-[11px] font-normal leading-tight"
                  onClick={() => {
                    setIbanInput(formatIbanForInput(ex.iban))
                    if (phase !== 'loading' && phase !== 'revealing') {
                      reset()
                    }
                  }}
                >
                  <span className="block truncate">{t(ex.nameKey)}</span>
                </Button>
              ))}
            </div>
          </div>
        </div>
        <Button
          type="submit"
          size="default"
          className="h-10 w-full rounded-lg text-sm font-medium"
          disabled={busyVisual}
        >
          {phase === 'revealing'
            ? t('form.submitRevealing')
            : phase === 'loading'
              ? t('form.submitLoading')
              : t('form.submit')}
        </Button>
      </form>

      <div id="result-region" aria-live="polite" className="min-h-[3.5rem]">
        {phase === 'loading' && (
          <div className="space-y-2 rounded-lg border border-border/50 bg-muted/20 p-3">
            <Skeleton className="h-4 w-2/5 rounded-md" />
            <Skeleton className="h-3 w-full max-w-[14rem] rounded-md" />
            <Skeleton className="h-3 w-3/5 rounded-md" />
            <Skeleton className="h-8 w-full max-w-[18rem] rounded-md" />
            <Skeleton className="h-3 w-4/5 rounded-md" />
          </div>
        )}

        <AnimatePresence mode="wait">
          {!busyVisual && phase === 'error' && errorMessage && (
            <motion.div
              key="iban-error"
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -6 }}
              transition={{ duration: 0.2 }}
            >
              <Alert
                variant="destructive"
                className="rounded-lg border-destructive/40 bg-destructive/10 px-3 py-2"
              >
                <AlertDescription className="text-xs leading-snug">
                  {errorMessage}
                </AlertDescription>
              </Alert>
            </motion.div>
          )}

          {showResultPanel && result && (
            <IbanValidateResultDetails
              key="iban-ok"
              result={result}
              revealActive={phase === 'revealing'}
            />
          )}
        </AnimatePresence>
      </div>
    </div>
  )
}
