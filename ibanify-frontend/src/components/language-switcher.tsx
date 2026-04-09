import { useTranslation } from 'react-i18next'
import DE from 'country-flag-icons/react/3x2/DE'
import GB from 'country-flag-icons/react/3x2/GB'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { cn } from '@/lib/utils'

const LOCALES = ['en', 'de'] as const

type Locale = (typeof LOCALES)[number]

function LocaleFlag({
  locale,
  className,
}: {
  locale: Locale
  className?: string
}) {
  return (
    <span
      className={cn(
        'inline-flex h-3.5 w-[1.375rem] shrink-0 overflow-hidden rounded-sm ring-1 ring-border/60',
        className,
      )}
      aria-hidden
    >
      {locale === 'de' ? (
        <DE className="h-full w-full object-cover" />
      ) : (
        <GB className="h-full w-full object-cover" />
      )}
    </span>
  )
}

export function LanguageSwitcher({ className }: { className?: string }) {
  const { i18n, t } = useTranslation()
  const active: Locale = i18n.resolvedLanguage?.startsWith('de') ? 'de' : 'en'

  return (
    <Select
      value={active}
      onValueChange={(v) => void i18n.changeLanguage(v)}
    >
      <SelectTrigger
        aria-label={t('lang.switch')}
        className={cn(
          'h-9 w-max max-w-full shrink-0 gap-1.5 border-border/80 px-2.5 py-0 [&_[data-slot=select-value]]:line-clamp-none [&_[data-slot=select-value]]:shrink-0',
          className,
        )}
      >
        <SelectValue className="text-[13px] whitespace-nowrap" />
      </SelectTrigger>
      <SelectContent align="end" className="min-w-[var(--radix-select-trigger-width)]">
        {LOCALES.map((lng) => (
          <SelectItem
            key={lng}
            value={lng}
            textValue={t(`lang.${lng}`)}
            className="py-1.5 text-[13px]"
          >
            <LocaleFlag locale={lng} />
            {t(`lang.${lng}`)}
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  )
}
