//
//  WeatherError.swift
//  Weather
//
//  Created by Artem Mkrtchyan on 9/27/23.
//

import Foundation

public enum WeatherError: Error {
    case faildToDecodeIncomingJSON
    case badStatusCode(statusCode: Int)
    case errorResponse(statusCode: Int, error: String)
    case unexpectedResponse(_ response: URLResponse?)
    case dataEmpty
    case anyError
}
