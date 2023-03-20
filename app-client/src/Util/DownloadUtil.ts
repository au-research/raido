/* Taken from https://stackoverflow.com/a/40657767/924597.
Literal copy/paste without thought: if it's busted, fix it. 
*/
export function escapeCsvField(text: string|undefined){
  if( !text ){
    return "";
  }
  
  return text.replace(/\\/g, "\\\\").
    replace(/\n/g, "\\n").
    replace(/,/g, "\\,");
}

