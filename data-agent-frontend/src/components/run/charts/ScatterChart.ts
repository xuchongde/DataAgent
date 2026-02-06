/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as echarts from 'echarts';
import { BaseChart, generateUniqueColors } from './BaseChart';

export class ScatterChart extends BaseChart {
  private chartInstance: echarts.ECharts | null = null;

  constructor(id: string, name: string) {
    super(id, name);
  }

  render(): void {
    if (!this.data || this.data.length === 0) {
      return;
    }

    const container = document.getElementById(this.id);
    if (!container) {
      return;
    }

    // 获取散点图的 x、y 轴配置
    const xAxis = this.axis.find(axis => axis.type === 'x') || this.axis[0];
    const yAxis = this.axis.find(axis => axis.type === 'y') || this.axis[1];

    if (!xAxis || !yAxis) {
      return;
    }

    // 生成足够的唯一颜色
    const colors: string[] = generateUniqueColors(this.data.length);

    // 将数据转换为散点图格式
    const scatterData = this.data.map((item, index) => {
      const xValue = parseFloat(item[xAxis.value]);
      const yValue = parseFloat(item[yAxis.value]);
      
      // 如果有第三维数据（如 size、value 等），可用于控制散点大小
      let sizeValue = 10; // 默认大小
      const sizeAxis = this.axis.find(axis => axis.type === 'size'); // 假设存在 size 轴
      if (sizeAxis && item[sizeAxis.value] !== undefined) {
        sizeValue = Math.max(5, Math.min(50, parseFloat(item[sizeAxis.value]) || 10));
      }

      return [
        isNaN(xValue) ? 0 : xValue,
        isNaN(yValue) ? 0 : yValue,
        sizeValue,
        item[xAxis.value], // 保留原始 x 数据用于提示
        item[yAxis.value]  // 保留原始 y 数据用于提示
      ];
    });

    if (!this.chartInstance) {
      this.chartInstance = echarts.init(container);
    }

    const option: echarts.EChartsOption = {
      title: {
        text: this._name || '散点图',
        left: 'center',
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          label: {
            backgroundColor: '#6a7985'
          }
        },
        formatter: (params: any) => {
          const param = params[0]; // 散点图只有一个系列
          const data = param.data;
          return `${param.seriesName}<br/>
                  X: ${data[3]}<br/>
                  Y: ${data[4]}<br/>
                  Size: ${data[2]}`;
        }
      },
      xAxis: {
        type: 'value',
        name: xAxis.label || xAxis.value,
        splitLine: {
          lineStyle: {
            type: 'dashed'
          }
        }
      },
      yAxis: {
        type: 'value',
        name: yAxis.label || yAxis.value,
        splitLine: {
          lineStyle: {
            type: 'dashed'
          }
        }
      },
      series: [{
        name: this._name || '散点图',
        type: 'scatter',
        data: scatterData,
        symbolSize: function (val: number[]) {
          // val 是 [x, y, size, ...]，这里用第3项作为大小
          return val[2];
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            borderColor: '#fff',
            borderWidth: 1
          }
        }
      }]
    };

    this.chartInstance.setOption(option);
  }

  destroy(): void {
    if (this.chartInstance) {
      this.chartInstance.dispose();
      this.chartInstance = null;
    }
  }

  resize(): void {
    if (this.chartInstance) {
      this.chartInstance.resize();
    }
  }
}