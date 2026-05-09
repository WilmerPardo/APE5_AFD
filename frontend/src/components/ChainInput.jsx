import { Play, RotateCcw } from 'lucide-react';
import { useState } from 'react';

export default function ChainInput({ selectedType, loading, onValidate }) {
  const [value, setValue] = useState('');

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
          placeholder={selectedType === 'telemetria' ? 'rhtc' : 'KGXF'}
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
    </form>
  );
}
