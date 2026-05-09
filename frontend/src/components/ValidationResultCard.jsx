import { CheckCircle2, CircleSlash, ShieldCheck } from 'lucide-react';

const rows = [
  ['AFND', 'resultadoAfnd'],
  ['AFD', 'resultadoAfd'],
  ['AFD minimizado', 'resultadoAfdMinimizado']
];

function ResultBadge({ value }) {
  const accepts = value === 'Acepta';
  const Icon = accepts ? CheckCircle2 : CircleSlash;

  // Usa el mismo componente visual para Acepta, Rechaza o sin validar.
  return (
    <span className={`result-badge ${accepts ? 'accept' : 'reject'}`}>
      <Icon size={16} aria-hidden="true" />
      {value || 'Sin validar'}
    </span>
  );
}

export default function ValidationResultCard({ result }) {
  return (
    <section className="panel validation-panel">
      <div className="section-title">
        <ShieldCheck size={20} aria-hidden="true" />
        <h2>Validacion</h2>
      </div>

      <div className="result-grid">
        {rows.map(([label, key]) => (
          <div className="result-row" key={key}>
            <span>{label}</span>
            <ResultBadge value={result?.[key]} />
          </div>
        ))}
      </div>

      {result && (
        <div className={`equivalence-pill ${result.equivalentes ? 'ok' : 'bad'}`}>
          {result.equivalentes ? 'Equivalentes' : 'Diferencia detectada'}
        </div>
      )}
    </section>
  );
}
