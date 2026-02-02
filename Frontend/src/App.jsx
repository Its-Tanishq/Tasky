import Landing from './Pages/Landing'
import ThemeProvider from './Context/ThemeContext'

function App() {

  return (
    <>
      <ThemeProvider>
        <Landing />
      </ThemeProvider>
    </>
  )
}

export default App
