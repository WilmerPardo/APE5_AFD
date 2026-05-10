import { Play, RotateCcw } from 'lucide-react';
import { useState } from 'react';

export default function ChainInput({ selectedType, loading, onValidate }) {
  const [value, setValue] = useState('');

  const examples = {
    telemetria: ['rhtc', 'rrhc', 'r'],
    bioinformatica: ['KGXF', 'KX', 'XF'],
    ecommerce: ['HSC', 'SC', 'HHHSC']
  };
  const currentExamples = examples[selectedType] || [];

  function handleSubmit(event) {
    event.preventDefault();
    // La cadena se valida sin recargar la pagina.
    onValidate(value);
  }

  function handleReset() {
    setValue('');
    onValidate('');
  }

  return (
    <form className="chain-form" onSubmit={handleSubmit}>
      <label htmlFor="cadena">Cadena</label>
      <div className="input-row">
        <input
          id="cadena"
          name="cadena"
          autoComplete="off"
          placeholder={selectedType === 'telemetria' ? 'rhtc' : selectedType === 'bioinformatica' ? 'KGXF' : 'HSC'}
          value={value}
          onChange={(event) => setValue(event.target.value)}
        />
        <button className="icon-button primary" disabled={loading} type="submit" title="Validar">
          <Play size={18} aria-hidden="true" />
          <span>Validar</span>
        </button>
        <button className="icon-button" disabled={loading} type="button" onClick={handleReset} title="Vaciar">
          <RotateCcw size={18} aria-hidden="true" />
        </button>
      </div>
      
      {currentExamples.length > 0 && (
        <div style={{ display: 'flex', gap: '8px', marginTop: '10px', flexWrap: 'wrap', alignItems: 'center' }}>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', fontWeight: '600' }}>Ejemplos:</span>
          {currentExamples.map((ex) => (
            <button
              key={ex}
              type="button"
              className="type-chip"
              style={{ cursor: 'pointer', minWidth: 'auto', minHeight: '24px', fontSize: '0.75rem', padding: '2px 8px' }}
              onClick={() => setValue(ex)}
            >
              {ex}
            </button>
          ))}
        </div>
      )}
    </form>
  );
}
