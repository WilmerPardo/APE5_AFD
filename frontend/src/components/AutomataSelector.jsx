import { Activity, Dna } from 'lucide-react';

const options = [
  { value: 'telemetria', label: 'Telemetria', icon: Activity, alphabet: 'r h t c' },
  { value: 'bioinformatica', label: 'Bioinformatica', icon: Dna, alphabet: 'X K G F' }
];

export default function AutomataSelector({ value, onChange }) {
  return (
    <div className="selector" aria-label="Seleccion de automata">
      {options.map((option) => {
        const Icon = option.icon;
        const active = value === option.value;

        return (
          <button
            className={`selector-button ${active ? 'active' : ''}`}
            key={option.value}
            onClick={() => onChange(option.value)}
            type="button"
          >
            <Icon size={18} aria-hidden="true" />
            <span>{option.label}</span>
            <small>{option.alphabet}</small>
          </button>
        );
      })}
    </div>
  );
}
