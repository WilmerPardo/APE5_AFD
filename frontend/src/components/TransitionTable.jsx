import { Table2 } from 'lucide-react';

export default function TransitionTable({ title, rows = [], alphabet = [] }) {
  // Si no llega alfabeto explicito, se infiere desde la primera fila.
  const symbols = alphabet.length ? alphabet : Object.keys(rows[0]?.transitions || {});

  return (
    <section className="panel table-panel">
      <div className="section-title">
        <Table2 size={20} aria-hidden="true" />
        <h2>{title}</h2>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Estado</th>
              <th>Subconjunto</th>
              {symbols.map((symbol) => (
                <th key={symbol}>{symbol}</th>
              ))}
              <th>Tipo</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.state}>
                <td className="state-cell">{row.state}</td>
                <td>{row.subset || '-'}</td>
                {symbols.map((symbol) => (
                  <td key={`${row.state}-${symbol}`}>{row.transitions?.[symbol] || '-'}</td>
                ))}
                <td>
                  <span className="type-chip">{row.type}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
