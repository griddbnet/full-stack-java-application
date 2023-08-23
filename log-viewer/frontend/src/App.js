import { useState, useEffect, useRef } from 'react';
import { RadioButtonContext } from './components/RadioButtonContext'
import { DateRangeContext } from './components/DateRangeContext';

import DataTable from './components/DataTable';
import TimePicker from './components/TimePicker'
import Sidebar from './components/Sidebar';

import { Table } from 'flowbite-react';
import moment from 'moment';

const HOST = "/api/";

const extractHostname = (str) => typeof str === 'string' ? str.split('_')[1] : "";
const extractLogType = (str) => typeof str === 'string' ? str.split('_')[2] : "";

const formatLogData = containers => {
  var rows = [];

  if (Object.keys(containers).length > 0) {
    for (const container in containers) {
      let contName = extractLogType(container);
      rows.push(containers[container].map((log, idx) => (
        <Table.Row className="bg-white dark:border-gray-700 dark:bg-gray-800" key={idx}>
          <Table.Cell className="whitespace-nowrap font-medium text-gray-900 dark:text-white">
            {log.ts}
          </Table.Cell>
          <Table.Cell>
            {log.hostname}
          </Table.Cell>
          <Table.Cell>
            {contName}
          </Table.Cell>
          <Table.Cell>
            {log.log}
          </Table.Cell>
        </Table.Row>
      )))
    }
  } else {
    rows.push(
      <Table.Row className="bg-white dark:border-gray-700 dark:bg-gray-800" key={1}>
        <Table.Cell className="whitespace-nowrap font-medium text-gray-900 dark:text-white">
          No
        </Table.Cell>
        <Table.Cell>
          Data
        </Table.Cell>
        <Table.Cell>

        </Table.Cell>
        <Table.Cell>

        </Table.Cell>
      </Table.Row>
    )
  }

  return rows;
}

const useDidMountEffect = (func, deps) => {
  const didMount = useRef(false);
  useEffect(() => {
    if (didMount.current) {
      func();
    } else {
      didMount.current = true;
    }
  }, deps);
};

const App = () => {

  const useMountEffect = (fun) => useEffect(fun, [])
  const [error, setError] = useState(null);
  const [isLoaded, setIsLoaded] = useState("true");
  const [logData, setLogData] = useState(null);
  const [Containers, setContainers] = useState([]);
  const [LogTypes, setLogTypes] = useState([]);
  const [Hostnames, setHostnames] = useState([]);



  useEffect(() => { document.body.style.backgroundColor = 'rgb(226 232 240)' }, [])

  useMountEffect(() => {
    console.log("getContainers: ", `${HOST}getContainers`)
    fetch(`${HOST}getContainers`)
      .then(res => res.json())
      .then(
        (result) => {
          setContainers(result);
          const arrLogTypes = ["none"];
          const arrHostnames = ["none"];
          result.forEach(container => {
            arrLogTypes.push(extractLogType(container))
            arrHostnames.push(extractHostname(container))
          })
          const uniqueHostnames = [...new Set(arrHostnames)];
          const uniqueLogTypes = [...new Set(arrLogTypes)]
          setHostnames(uniqueHostnames);
          setLogTypes(uniqueLogTypes)

        },
        (error) => {
          setError(error);
        }
      )
  }, [])

  const queryGridDB = (hostname, logtype, range) => {
    let start = range.start.valueOf();
    let end = range.end.valueOf();

    // if both Logype and Hostname are still none, do not fetch

    let queryStr = `${HOST}containersWithParameters?hostname=${hostname}&logtype=${logtype}&start=${start}&end=${end}`
    console.log(queryStr)

    fetch(queryStr)
      .then(res => res.json())
      .then(
        (result) => {
          console.log("results of getting hostname or logtypes: ", result)
          const formattedData = formatLogData(result);
          setLogData(formattedData)
        },
        (error) => {
          setError(error);
        }
      )


  }

  const [selectHostname, setSelectedHostname] = useState("none");
  const [selectLogType, setSelectedLogType] = useState("none");
  const now = new Date();

  const start = moment(
    new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0, 0)
  );

  const end = moment(start).add(1, 'days').subtract(1, 'seconds');

  const [range, setRange] = useState({
    start: start,
    end: end,
  });

  useDidMountEffect(() => {
    queryGridDB(selectHostname, selectLogType, range);
  }, [range, selectHostname, selectLogType])


  if (error) {
    return <div>Error: {error.message}</div>;
  } else if (!isLoaded) {
    return <div>Loading...</div>;
  } else {
    return (
      <div className="container px-32">
        <RadioButtonContext.Provider value={{ setSelectedHostname, setSelectedLogType, selectHostname, selectLogType }}>

          <div className='p-5'>
            <Sidebar logTypes={LogTypes} hostnames={Hostnames} />
          </div>

          <div className="flex justify-end">

            <DateRangeContext.Provider value={{ start, end, range, setRange }}>
              <div>
                < TimePicker />
              </div>
            </DateRangeContext.Provider>
          </div>

          <div className='flex flex-row justify-center py-10'>
            <DataTable logData={logData} />
          </div>


        </RadioButtonContext.Provider >
      </div>
    );
  }



}

export default App;
