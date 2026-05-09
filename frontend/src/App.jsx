import { useCallback, useEffect, useMemo, useState } from 'react';
import { AlertTriangle, GitCompareArrows, Loader2 } from 'lucide-react';
import { getAutomata, validateChain } from './api/automataApi.js';
import AutomataSelector from './components/AutomataSelector.jsx';
import ChainInput from './components/ChainInput.jsx';
import DiagramViewer from './components/DiagramViewer.jsx';
import TransitionTable from './components/TransitionTable.jsx';
import ValidationResultCard from './components/ValidationResultCard.jsx';

const titles = {
  telemetria: 'Telemetria',
  bioinformatica: 'Bioinformatica'
};

export default function App() {
  const [selectedType, setSelectedType] = useState('telemetria');
  const [automataInfo, setAutomataInfo] = useState(null);
  const [validation, setValidation] = useState(null);
  const [loading, setLoading] = useState(false);
  const [validating, setValidating] = useState(false);
  const [error, setError] = useState('');

  const alphabet = useMemo(
    () => Array.from(automataInfo?.afd?.alphabet || []),
    [automataInfo]
  );

  // Carga la definicion completa del ejercicio seleccionado.
  const loadAutomata = useCallback(async (type) => {
    setLoading(true);
    setError('');
    setValidation(null);

    try {
      const info = await getAutomata(type);
      setAutomataInfo(info);
    } catch (requestError) {
      setError('No se pudo conectar con el backend en http://localhost:8080.');
      setAutomataInfo(null);
      console.error(requestError);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadAutomata(selectedType);
  }, [selectedType, loadAutomata]);

  // Envia la cadena al backend y guarda la comparacion AFND/AFD/minimizado.
  async function handleValidate(cadena) {
    setValidating(true);
    setError('');

    try {
      const result = await validateChain(selectedType, cadena);
      setValidation(result);
    } catch (requestError) {
      setError('No se pudo validar la cadena.');
      console.error(requestError);
    } finally {
      setValidating(false);
    }
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Practica 4</p>
          <h1>Conversion AFND a AFD</h1>
        </div>
        <AutomataSelector value={selectedType} onChange={setSelectedType} />
      </header>

      {error && (
        <div className="error-banner">
          <AlertTriangle size={18} aria-hidden="true" />
          <span>{error}</span>
        </div>
      )}

      <section className="workspace">
        <div className="left-column">
          <section className="panel control-panel">
            <div className="section-title">
              <GitCompareArrows size={20} aria-hidden="true" />
              <h2>{titles[selectedType]}</h2>
            </div>

            <ChainInput
              selectedType={selectedType}
              loading={loading || validating}
              onValidate={handleValidate}
            />

            <div className="summary-strip">
              <span>{automataInfo?.afd?.states?.length || 0} estados AFD</span>
              <span>{automataInfo?.afdMinimizado?.states?.length || 0} estados min.</span>
              <span>{automataInfo?.estadosFusionados?.join(' / ') || 'Sin fusiones'}</span>
            </div>
          </section>

          <ValidationResultCard result={validation} />
          <DiagramViewer type={selectedType} />
        </div>

        <div className="right-column">
          {loading ? (
            <div className="loading-box">
              <Loader2 size={26} className="spin" aria-hidden="true" />
              <span>Cargando automatas</span>
            </div>
          ) : (
            <>
              <TransitionTable
                title="AFD por subconjuntos"
                rows={automataInfo?.tablaAfd || []}
                alphabet={alphabet}
              />
              <TransitionTable
                title="AFD minimizado"
                rows={automataInfo?.tablaAfdMinimizado || []}
                alphabet={alphabet}
              />
            </>
          )}
        </div>
      </section>
    </main>
  );
}
