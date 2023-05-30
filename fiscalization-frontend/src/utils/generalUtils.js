export const roundToTwoDecimalPlaces = (number) => {
  let output = number.toString()
  const decimalPointIndex = output.indexOf('.')

  if (decimalPointIndex === -1) return output + '.00'

  const thirdDecimalPlace = output.at(decimalPointIndex + 3)

  if (!thirdDecimalPlace) {
    while (!output.at(decimalPointIndex + 2)) {
      output += '0'
    }
    return output
  }

  if (['0', '1', '2', '3', '4'].includes(thirdDecimalPlace)) {
    return output.substring(0, decimalPointIndex + 3)
  } else {
    const secondDecimal = parseInt(output.at(decimalPointIndex + 2)) + 1
    return output.substring(0, decimalPointIndex + 2) + secondDecimal
  }
}
