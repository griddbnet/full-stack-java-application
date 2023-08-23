import {  Table } from 'flowbite-react';

function DataTable({logData}) {
    return (
      <Table striped hoverable>
        <Table.Head>
          <Table.HeadCell>
            Time
          </Table.HeadCell>
          <Table.HeadCell>
            Hostname
          </Table.HeadCell>
          <Table.HeadCell>
            Log Type
          </Table.HeadCell>
          <Table.HeadCell>
            Log
          </Table.HeadCell>
        </Table.Head>
        <Table.Body className="divide-y">
          {logData}
        </Table.Body>
      </Table>
    )
  }

  export default DataTable;