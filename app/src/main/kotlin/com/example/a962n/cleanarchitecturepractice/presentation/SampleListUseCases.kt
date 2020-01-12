package com.example.a962n.cleanarchitecturepractice.presentation

import com.example.a962n.domain.useCase.sample.AsyncGetSampleList

/**
 * 画面単位のユースケースをまとめるためのホルダークラス
 * ユースケースの数によってViewModelの引数が増えてしまうのを抑えるため
 */
data class SampleListUseCases constructor(val getSampleList: AsyncGetSampleList)