import { motion } from 'motion/react'
import { useTranslation } from 'react-i18next'
import { ScrambleSpan } from '@/components/iban/scramble-span'
import { getIbanStructureSegments } from '@/lib/iban-structure'

export function IbanStructureBreakdown({
  normalizedIban,
  revealActive = false,
  segmentDelays,
}: {
  normalizedIban: string
  revealActive?: boolean
  segmentDelays?: number[]
}) {
  const { t } = useTranslation()
  const segments = getIbanStructureSegments(normalizedIban)

  return (
    <div className="border-border/50 border-t pt-2">
      <p className="text-muted-foreground mb-1 text-[10px] font-medium uppercase tracking-wider">
        {t('structure.title')}
      </p>
      <div className="overflow-x-auto [-ms-overflow-style:none] [scrollbar-width:thin]">
        <div className="border-border/55 bg-muted/20 ring-border/40 inline-flex w-max max-w-full flex-nowrap rounded-md border shadow-[inset_0_1px_0_0_rgba(255,255,255,0.04)] ring-1 ring-inset">
          {segments.map((seg, i) => {
            const label = t(`structure.${seg.kind}`)
            const delay = segmentDelays?.[i] ?? 0
            return (
              <motion.div
                key={`${seg.kind}-${i}-${seg.value.slice(0, 12)}`}
                className={`flex min-w-0 flex-col items-center gap-0.5 px-2 py-1.5 transition-colors hover:bg-muted/35 sm:px-3 sm:py-2 ${i > 0 ? 'border-border/45 border-l' : ''}`}
                title={`${label}: ${seg.value}`}
                initial={{ opacity: 0, y: 4 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{
                  delay: i * 0.04,
                  duration: 0.2,
                  ease: [0.22, 1, 0.36, 1],
                }}
              >
                <span className="text-foreground font-mono text-xs font-semibold tracking-wide whitespace-nowrap tabular-nums sm:text-[13px]">
                  <ScrambleSpan
                    text={seg.value}
                    reveal={revealActive}
                    startDelayMs={delay}
                  />
                </span>
                <span className="text-muted-foreground max-w-[10rem] text-center text-[8px] font-medium uppercase leading-tight tracking-wide sm:text-[9px]">
                  {label}
                </span>
              </motion.div>
            )
          })}
        </div>
      </div>
    </div>
  )
}
