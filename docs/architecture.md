FileSystem
- def read: Path => InputStream
- def write: Path => InputStream => Unit
-------------------------------------------------------------
Archive
- case class FileInfo(Path)
- def read: InputStream => Stream[(FileInfo, InputStream)]
-------------------------------------------------------------
Table(Schema, Stream[Row])
Column
Schema(Seq[Column])
type Row = Array[Any]
-------------------------------------------------------------
CSV
- def read: InputStream => Table
- def write: Table => InputStream
- def schema: InputStream => Schema
-------------------------------------------------------------
Hub
- Config
- def hub: Config => Data => Data
-------------------------------------------------------------
Link
- Config
- def link: Config => Data => Data
-------------------------------------------------------------
Satellite
- Config
- def satellite: Config => Data => Data
-------------------------------------------------------------

Scenarios

ExtractFilesFromZip
    FileSystem.read
    | Archive.read
    | 
    
GenerateDataModel
    FileSystem.read
    | Archive.read
    | 

GenerateHubConfig

GenerateHub
