import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { motion } from 'motion/react'
import { CheckCircle2, XCircle } from 'lucide-react'
import { ScrambleSpan } from '@/components/iban/scramble-span'
import { IbanStructureBreakdown } from '@/components/iban/iban-structure-breakdown'
import { buildResultRevealDelays, REVEAL_TICK_MS } from '@/lib/iban-result-reveal'
import { cn } from '@/lib/utils'
import type { ValidateIbanResponse } from '@/types/iban-api'

export function IbanValidateResultDetails({
  result,
  revealActive,
}: {
  result: ValidateIbanResponse
  revealActive: boolean
}) {
  const { t } = useTranslation()
  const delays = useMemo(() => buildResultRevealDelays(result), [result])
  const bankLogoSrc =
    result.bankLogoUrl != null && result.bankLogoUrl.trim() !== ''
      ? result.bankLogoUrl.trim()
      : null
  const hasBankLogo = bankLogoSrc !== null
  const hasBankBlock =
    (result.bankName != null && result.bankName !== '') ||
    (result.bic != null && result.bic !== '') ||
    (result.bankCode != null && result.bankCode !== '') ||
    (result.bankCity != null && result.bankCity !== '') ||
    (result.bankZip != null && result.bankZip !== '') ||
    hasBankLogo

  return (
    <motion.div
      key="iban-result"
      layout
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -8 }}
      transition={{ duration: 0.22 }}
      className="rounded-lg border border-border/60 bg-muted/15 p-3"
    >
      <div className="flex gap-2">
        <div className="shrink-0 pt-px">
          {result.valid ? (
            <CheckCircle2 className="size-7 text-emerald-400/95" aria-hidden />
          ) : (
            <XCircle className="size-7 text-amber-500/95" aria-hidden />
          )}
        </div>
        <div className="min-w-0 flex-1 space-y-2">
          <div>
            <p className="text-foreground text-sm font-semibold tracking-tight">
              {result.valid ? t('result.valid') : t('result.invalid')}
            </p>
            {!result.valid && result.messages.length > 0 && (
              <ul className="text-muted-foreground mt-1 list-inside list-disc space-y-0.5 text-xs leading-snug">
                {result.messages.map((m, i) => (
                  <li key={`err-${i}-${m.slice(0, 24)}`}>{m}</li>
                ))}
              </ul>
            )}
          </div>

          <IbanStructureBreakdown
            normalizedIban={result.normalizedIban}
            revealActive={revealActive}
            segmentDelays={delays.segments}
          />

          {hasBankBlock && (
            <div className="border-border/50 border-t pt-2 text-xs">
              <p className="text-muted-foreground mb-1.5 text-[10px] font-medium uppercase tracking-wider">
                {t('result.bankDetails')}
              </p>
              <div
                className={cn(
                  'min-w-0',
                  hasBankLogo && 'flex items-start gap-2',
                  !hasBankLogo && 'space-y-1.5',
                )}
              >
                {hasBankLogo && (
                  <img
                    src={bankLogoSrc}
                    alt={
                      result.bankName != null && result.bankName !== ''
                        ? t('result.logoAltNamed', { name: result.bankName })
                        : t('result.logoAlt')
                    }
                    className="h-9 w-auto max-w-[9rem] shrink-0 object-contain object-left"
                    loading="lazy"
                    decoding="async"
                  />
                )}
                <div
                  className={cn(
                    'min-w-0 space-y-1.5',
                    hasBankLogo && 'flex-1 pt-0.5',
                  )}
                >
                  {result.bankName != null && result.bankName !== '' && (
                    <p className="text-foreground text-sm font-semibold leading-snug">
                      <ScrambleSpan
                        text={result.bankName}
                        reveal={revealActive}
                        startDelayMs={delays.bankName ?? 0}
                        tickMs={REVEAL_TICK_MS}
                      />
                    </p>
                  )}
                  <div className="text-muted-foreground grid grid-cols-1 gap-x-4 gap-y-1 text-[11px] sm:grid-cols-2">
                    {result.bankZip != null && result.bankZip !== '' && (
                      <div className="min-w-0">
                        <p className="text-[10px] font-medium uppercase tracking-wider">
                          {t('result.postalCode')}
                        </p>
                        <p className="text-foreground font-mono text-xs">
                          <ScrambleSpan
                            text={result.bankZip}
                            reveal={revealActive}
                            startDelayMs={delays.bankZip ?? 0}
                            tickMs={REVEAL_TICK_MS}
                          />
                        </p>
                      </div>
                    )}
                    {result.bankCity != null && result.bankCity !== '' && (
                      <div className="min-w-0">
                        <p className="text-[10px] font-medium uppercase tracking-wider">
                          {t('result.city')}
                        </p>
                        <p className="text-foreground text-xs leading-snug">
                          <ScrambleSpan
                            text={result.bankCity}
                            reveal={revealActive}
                            startDelayMs={delays.bankCity ?? 0}
                            tickMs={REVEAL_TICK_MS}
                          />
                        </p>
                      </div>
                    )}
                    {result.bankCode != null && result.bankCode !== '' && (
                      <div className="min-w-0">
                        <p className="text-[10px] font-medium uppercase tracking-wider">
                          {t('result.bankCode')}
                        </p>
                        <p className="text-foreground font-mono text-xs">
                          <ScrambleSpan
                            text={result.bankCode}
                            reveal={revealActive}
                            startDelayMs={delays.bankCode ?? 0}
                            tickMs={REVEAL_TICK_MS}
                          />
                        </p>
                      </div>
                    )}
                    {result.bic != null && result.bic !== '' && (
                      <div className="min-w-0 sm:col-span-2">
                        <p className="text-[10px] font-medium uppercase tracking-wider">
                          {t('result.bic')}
                        </p>
                        <p className="text-foreground font-mono text-xs">
                          <ScrambleSpan
                            text={result.bic}
                            reveal={revealActive}
                            startDelayMs={delays.bic ?? 0}
                            tickMs={REVEAL_TICK_MS}
                          />
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </motion.div>
  )
}
